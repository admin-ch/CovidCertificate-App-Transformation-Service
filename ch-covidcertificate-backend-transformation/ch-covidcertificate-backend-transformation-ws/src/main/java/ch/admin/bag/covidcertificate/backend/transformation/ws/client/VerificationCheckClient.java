package ch.admin.bag.covidcertificate.backend.transformation.ws.client;

import ch.admin.bag.covidcertificate.backend.transformation.model.HCertPayload;
import ch.admin.bag.covidcertificate.backend.transformation.model.VerificationResponse;
import ch.admin.bag.covidcertificate.sdk.core.models.healthcert.DccHolder;
import ch.admin.bag.covidcertificate.sdk.core.models.state.CheckSignatureState;
import com.fasterxml.jackson.databind.ObjectMapper;
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
                        .registerModule(new JavaTimeModule());
    }

    private VerificationResponse verify(HCertPayload hCertPayload) {
        final String hCert;
        try {
            hCert = objectMapper.writeValueAsString(hCertPayload);
            final var request =
                    HttpRequest.newBuilder(new URI(baseurl + verifyEndpoint))
                            .POST(BodyPublishers.ofString(hCert))
                            .build();
            final HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            if (response.statusCode() == 400) {
                logger.info("Certificate couldn't be decoded.");
                return null;
            }
            return objectMapper.readValue(response.body(), VerificationResponse.class);
        } catch (URISyntaxException | IOException | InterruptedException e) {
            logger.error("Couldn't verify certificate", e);
            return null;
        }
    }

    /**
     * Decode and verify a client HCert
     *
     * @param hCertPayload payload as sent with the original request
     * @return the decoded certificate if it decodeable and valid, null otherwise
     */
    public DccHolder isValid(HCertPayload hCertPayload) {
        final var verificationResponse = verify(hCertPayload);
        if (verificationResponse != null && verificationResponse.getSuccessState() != null) {
            return verificationResponse.getHcertDecoded();
        } else {
            return null;
        }
    }

    /**
     * Decode a client HCert and verify its signature
     *
     * @param hCertPayload payload as sent with the original request
     * @return the decoded certificate if it decodeable and its signature is valid, null otherwise
     */
    public DccHolder isValidSig(HCertPayload hCertPayload) {
        final var verificationResponse = verify(hCertPayload);
        if (verificationResponse != null
                && (verificationResponse.getSuccessState() != null
                        || (verificationResponse.getInvalidState() != null
                                && verificationResponse.getInvalidState().getSignatureState()
                                        instanceof CheckSignatureState.SUCCESS))) {
            return verificationResponse.getHcertDecoded();
        }
        return null;
    }
}
