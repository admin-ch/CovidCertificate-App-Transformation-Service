package ch.admin.bag.covidcertificate.backend.transformation.ws.client;

import ch.admin.bag.covidcertificate.backend.transformation.model.CertLightPayload;
import ch.admin.bag.covidcertificate.backend.transformation.model.Person;
import ch.admin.bag.covidcertificate.backend.transformation.model.TransformPayload;
import ch.admin.bag.covidcertificate.backend.transformation.ws.util.OauthWebClient;
import ch.admin.bag.covidcertificate.sdk.core.models.healthcert.eu.Eudgc;
import ch.admin.bag.covidcertificate.sdk.core.verifier.nationalrules.ValidityRange;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import org.springframework.http.MediaType;

public class CertLightClient {

    private final String lightCertificateEnpoint;
    private final OauthWebClient oauthWebClient;
    private final ObjectMapper objectMapper;
    private final ZoneId verificationZoneId;

    public CertLightClient(
            String lightCertificateEnpoint,
            OauthWebClient oauthWebClient,
            ZoneId verificationZoneId) {
        this.lightCertificateEnpoint = lightCertificateEnpoint;
        this.oauthWebClient = oauthWebClient;
        this.verificationZoneId = verificationZoneId;
        this.objectMapper = new ObjectMapper();
    }

    public CertLightPayload getCertLight(Eudgc euCert, ValidityRange validityRange)
            throws JsonProcessingException {

        var name = euCert.getPerson();

        var person = new Person();
        person.setFn(name.getFamilyName());
        person.setGn(name.getGivenName());
        person.setFnt(name.getStandardizedFamilyName());
        person.setGnt(name.getStandardizedGivenName());

        var transformPayload = new TransformPayload();
        transformPayload.setNam(person);
        transformPayload.setDob(euCert.getDateOfBirth());

        var exp =
                validityRange.getValidUntil().atZone(verificationZoneId).toInstant().toEpochMilli();
        var nowPlus48 = Instant.now().plus(Duration.ofHours(48)).toEpochMilli();
        transformPayload.setExp(Math.min(exp, nowPlus48));

        // Get and forward light certificate
        var transformResponse =
                oauthWebClient
                        .getWebClient()
                        .post()
                        .uri(lightCertificateEnpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(transformPayload)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
        return objectMapper.readValue(transformResponse, CertLightPayload.class);
    }
}
