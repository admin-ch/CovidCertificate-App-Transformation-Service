package ch.admin.bag.covidcertificate.backend.transformation.ws.client;

import ch.admin.bag.covidcertificate.backend.transformation.model.CertLightPayload;
import ch.admin.bag.covidcertificate.backend.transformation.model.TransformPayload;
import ch.admin.bag.covidcertificate.backend.transformation.ws.util.DccHelper;
import ch.admin.bag.covidcertificate.backend.transformation.ws.util.OauthWebClient;
import ch.admin.bag.covidcertificate.sdk.core.models.healthcert.eu.DccCert;
import ch.admin.bag.covidcertificate.sdk.core.verifier.nationalrules.ValidityRange;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import org.springframework.http.MediaType;

public class CertLightClient {

    private final String lightCertificateEndpoint;
    private final OauthWebClient oauthWebClient;
    private final ObjectMapper objectMapper;
    private final ZoneId verificationZoneId;

    public CertLightClient(
            String lightCertificateEndpoint,
            OauthWebClient oauthWebClient,
            ZoneId verificationZoneId) {
        this.lightCertificateEndpoint = lightCertificateEndpoint;
        this.oauthWebClient = oauthWebClient;
        this.verificationZoneId = verificationZoneId;
        this.objectMapper = new ObjectMapper();
    }

    public CertLightPayload getCertLight(DccCert euCert, ValidityRange validityRange)
            throws JsonProcessingException {

        var expiry =
                validityRange.getValidUntil().atZone(verificationZoneId).toInstant().toEpochMilli();
        var nowPlus48 = Instant.now().plus(Duration.ofHours(48)).toEpochMilli();

        var transformPayload = new TransformPayload();
        transformPayload.setNam(DccHelper.getPerson(euCert));
        transformPayload.setDob(euCert.getDateOfBirth());
        transformPayload.setExp(Math.min(expiry, nowPlus48));

        // Get and forward light certificate
        var transformResponse =
                oauthWebClient
                        .getWebClient()
                        .post()
                        .uri(lightCertificateEndpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(transformPayload)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
        return objectMapper.readValue(transformResponse, CertLightPayload.class);
    }
}
