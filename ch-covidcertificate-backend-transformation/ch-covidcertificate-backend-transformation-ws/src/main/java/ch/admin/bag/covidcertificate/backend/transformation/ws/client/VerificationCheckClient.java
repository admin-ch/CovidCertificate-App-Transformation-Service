package ch.admin.bag.covidcertificate.backend.transformation.ws.client;

import ch.admin.bag.covidcertificate.backend.transformation.model.HCertPayload;
import ch.admin.bag.covidcertificate.backend.transformation.model.VerificationResponse;
import ch.admin.bag.covidcertificate.backend.transformation.ws.client.deserializer.CustomCovidCertificateDeserializer;
import ch.admin.bag.covidcertificate.backend.transformation.ws.client.exceptions.ResponseParseError;
import ch.admin.bag.covidcertificate.backend.transformation.ws.client.exceptions.ValidationException;
import ch.admin.bag.covidcertificate.sdk.core.models.healthcert.CovidCertificate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class VerificationCheckClient {

    private static final Logger logger = LoggerFactory.getLogger(VerificationCheckClient.class);

    private final String baseurl;
    private final String verifyEndpoint;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public VerificationCheckClient(String baseurl, String verifyEndpoint) {
        this.baseurl = baseurl;
        this.verifyEndpoint = verifyEndpoint;
        httpClient = HttpClient.newHttpClient();

        objectMapper =
                new ObjectMapper()
                        .registerModule(new KotlinModule())
                        .registerModule(new JavaTimeModule())
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        var deserialization = new SimpleModule();
        deserialization.addDeserializer(
                CovidCertificate.class, new CustomCovidCertificateDeserializer());
        objectMapper.registerModule(deserialization);
    }

    /**
     * Decode and verify a client HCert
     *
     * @param hCertPayload payload as sent with the original request
     * @return the decoded certificate if it can be decoded and is valid, null if it can't be
     *     decoded
     * @throws InterruptedException thread is interrupted while waiting for HTTP response
     * @throws ValidationException certificate isn't valid
     * @throws ResponseParseError response from validation endpoint couldn't be parsed
     */
    public VerificationResponse validate(HCertPayload hCertPayload)
        throws InterruptedException, ValidationException, ResponseParseError {
        final var verificationResponse = verify(hCertPayload);
        if (verificationResponse != null && verificationResponse.getSuccessState() != null) {
            return verificationResponse;
        } else if (verificationResponse == null) {
            throw new ResponseParseError(null);
        } else {
            throw new ValidationException(
                verificationResponse.getErrorState() != null
                    ? verificationResponse.getErrorState()
                    : verificationResponse.getInvalidState());
        }
    }

    private VerificationResponse verify(HCertPayload hCertPayload)
            throws InterruptedException, ResponseParseError {
        final String hCert;
        try {
            hCert = objectMapper.writeValueAsString(hCertPayload);

            final var request =
                    HttpRequest.newBuilder(new URI(baseurl + verifyEndpoint))
                            .header("Content-Type", "application/json")
                            .POST(BodyPublishers.ofString(hCert))
                            .build();
            final HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

            if (response.statusCode() != HttpStatus.OK.value()) {
                logger.info("Certificate couldn't be decoded: HTTP {}", response.statusCode());
                return null;
            }

            return parseResponse(response);
        } catch (URISyntaxException | IOException e) {
            logger.error("Couldn't verify certificate", e);
            return null;
        }
    }

    private VerificationResponse parseResponse(HttpResponse<String> response)
            throws ResponseParseError, JsonProcessingException {
        try {
            return objectMapper.readValue(response.body(), VerificationResponse.class);
        } catch (JsonMappingException ex) {
            throw new ResponseParseError(objectMapper.readTree(response.body()));
        } catch (Exception e) {
            throw new ResponseParseError(null);
        }
    }
}
