package ch.admin.bag.covidcertificate.backend.transformation.ws.client;

import ch.admin.bag.covidcertificate.backend.transformation.model.lightcert.CertLightResponse;
import ch.admin.bag.covidcertificate.backend.transformation.model.lightcert.BitLightCertPayload;
import ch.admin.bag.covidcertificate.backend.transformation.ws.util.DccHelper;
import ch.admin.bag.covidcertificate.sdk.core.models.healthcert.eu.DccCert;
import ch.admin.bag.covidcertificate.sdk.core.verifier.nationalrules.ValidityRange;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

public class CertLightClient {

    private final ZoneId verificationZoneId;
    private final BitClient bitClient;

    public CertLightClient(ZoneId verificationZoneId, BitClient bitClient) {
        this.verificationZoneId = verificationZoneId;
        this.bitClient = bitClient;
    }

    public CertLightResponse getCertLight(DccCert euCert, ValidityRange validityRange)
            throws JsonProcessingException {

        var expiry =
                validityRange.getValidUntil().atZone(verificationZoneId).toInstant().toEpochMilli();
        var nowPlus48 = Instant.now().plus(Duration.ofHours(48)).toEpochMilli();

        var payload = new BitLightCertPayload();
        payload.setNam(DccHelper.getPerson(euCert));
        payload.setDob(euCert.getDateOfBirth());
        payload.setExp(Math.min(expiry, nowPlus48));

        return bitClient.getLightCert(payload);
    }
}
