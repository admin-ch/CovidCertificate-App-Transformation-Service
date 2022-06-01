package ch.admin.bag.covidcertificate.backend.transformation.ws.client;

import ch.admin.bag.covidcertificate.backend.transformation.model.HCertPayload;
import ch.admin.bag.covidcertificate.sdk.core.models.healthcert.eu.DccCert;

public class RenewalClient {
    private final BitClient bitClient;

    public RenewalClient(BitClient bitClient) {
        this.bitClient = bitClient;
    }

    public HCertPayload getRenewedCert(DccCert euCert) {
        return bitClient.getRenewedCert();
    }
}
