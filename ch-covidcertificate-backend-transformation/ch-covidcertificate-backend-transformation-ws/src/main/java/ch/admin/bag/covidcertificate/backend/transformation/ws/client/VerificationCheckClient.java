package ch.admin.bag.covidcertificate.backend.transformation.ws.client;

import ch.admin.bag.covidcertificate.backend.transformation.model.HCertPayload;
import ch.admin.bag.covidcertificate.backend.transformation.model.VerificationResponse;
import ch.admin.bag.covidcertificate.backend.transformation.ws.client.exceptions.ResponseParseError;
import ch.admin.bag.covidcertificate.backend.transformation.ws.client.exceptions.ValidationException;
import ch.admin.bag.covidcertificate.sdk.core.models.healthcert.CertificateHolder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class VerificationCheckClient {

    private static final Logger logger = LoggerFactory.getLogger(VerificationCheckClient.class);

    private final String baseurl;
    private final String verifyEndpoint;
    private final String verifyRenewalEndpoint;
    private final RestTemplate rt;
    private final ObjectMapper objectMapper;

    public VerificationCheckClient(
            String baseurl,
            String verifyEndpoint,
            String verificationCheckRenewalEndpoint,
            RestTemplate rt,
            ObjectMapper objectMapper) {
        this.baseurl = baseurl;
        this.verifyEndpoint = verifyEndpoint;
        this.verifyRenewalEndpoint = verificationCheckRenewalEndpoint;
        this.rt = rt;
        this.objectMapper = objectMapper;
    }

    /**
     * Decode and verify a client HCert for renewal
     *
     * @param hCertPayload payload as sent with the original request
     * @return the decoded certificate if it can be decoded and is valid for renewal, null if it
     *     can't be decoded
     * @throws ValidationException certificate isn't valid for renewal
     * @throws ResponseParseError response from validation endpoint couldn't be parsed
     */
    public VerificationResponse validateCertForRenewal(HCertPayload hCertPayload)
            throws ValidationException, ResponseParseError {
        return internalValidate(hCertPayload, VerificationType.RENEWAL);
    }

    /**
     * Decode and verify a client HCert
     *
     * @param hCertPayload payload as sent with the original request
     * @return the decoded certificate if it can be decoded and is valid, null if it can't be
     *     decoded
     * @throws ValidationException certificate isn't valid
     * @throws ResponseParseError response from validation endpoint couldn't be parsed
     */
    public VerificationResponse validate(HCertPayload hCertPayload)
            throws ValidationException, ResponseParseError {
        return internalValidate(hCertPayload, VerificationType.FULL);
    }

    /**
     * Decode and verify the signature a client HCert
     *
     * @param hCertPayload payload as sent with the original request
     * @return the decoded certificate if it can be decoded and is valid, null if it can't be
     *     decoded
     * @throws ValidationException certificate signature isn't valid
     * @throws ResponseParseError response from validation endpoint couldn't be parsed
     */
    public VerificationResponse validateSignature(HCertPayload hCertPayload)
            throws ValidationException, ResponseParseError {
        return internalValidate(hCertPayload, VerificationType.SIGNATURE_ONLY);
    }

    private VerificationResponse internalValidate(
            HCertPayload hCertPayload, VerificationType verificationType)
            throws ValidationException, ResponseParseError {
        final var verificationResponse = verify(hCertPayload, verificationType);
        if (verificationResponse == null) {
            throw new ResponseParseError(null);
        } else if (verificationResponse.isValid()
                || (VerificationType.SIGNATURE_ONLY.equals(verificationType)
                        && verificationResponse.signatureIsValid())) {
            return verificationResponse;
        } else {
            throw new ValidationException(
                    verificationResponse.getErrorState() != null
                            ? verificationResponse.getErrorState()
                            : verificationResponse.getInvalidState());
        }
    }

    private VerificationResponse verify(
            HCertPayload hCertPayload, VerificationType verificationType)
            throws ResponseParseError {
        try {
            final String endpoint =
                    VerificationType.RENEWAL.equals(verificationType)
                            ? verifyRenewalEndpoint
                            : verifyEndpoint;
            final var uri = UriComponentsBuilder.fromHttpUrl(baseurl + endpoint).build().toUri();
            final var request =
                    RequestEntity.post(uri).headers(createRequestHeaders()).body(hCertPayload);
            final var response = rt.exchange(request, String.class);
            return parseResponse(response, verificationType);
        } catch (JsonProcessingException e) {
            logger.error("Couldn't verify certificate", e);
        } catch (HttpStatusCodeException e) {
            logger.info("Certificate couldn't be verified", e);
        }
        return null;
    }

    private HttpHeaders createRequestHeaders() {
        var headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        return headers;
    }

    private VerificationResponse parseResponse(
            ResponseEntity<String> response, VerificationType verificationType)
            throws ResponseParseError, JsonProcessingException {
        try {
            return objectMapper.readValue(response.getBody(), VerificationResponse.class);
        } catch (JsonMappingException ex) {
            // json deserialization fails when invalid state or error state is not null
            JsonNode node = objectMapper.readTree(response.getBody());
            if (VerificationType.SIGNATURE_ONLY.equals(verificationType)
                    && signatureIsValid(node)) {
                VerificationResponse verificationResponse = new VerificationResponse();
                verificationResponse.setHcertDecoded(
                        objectMapper.treeToValue(
                                node.get("hcertDecoded"), CertificateHolder.class));
                return verificationResponse;
            } else {
                throw new ResponseParseError(node);
            }
        } catch (Exception e) {
            throw new ResponseParseError(null);
        }
    }

    private boolean signatureIsValid(JsonNode node) {
        JsonNode invalidState = node.get("invalidState");
        if (!node.get("successState").isNull()) { // success state -> everything is valid
            return true;
        } else if (invalidState.isNull()) {
            // when no invalid state is present, it is not possible to evaluate whether the
            // signature is valid
            return false;
        } else {
            // an empty signatureState object within the invalidState node means the signature was
            // valid
            JsonNode signatureState = invalidState.get("signatureState");
            return signatureState.isNull() || signatureState.isEmpty();
        }
    }
}
