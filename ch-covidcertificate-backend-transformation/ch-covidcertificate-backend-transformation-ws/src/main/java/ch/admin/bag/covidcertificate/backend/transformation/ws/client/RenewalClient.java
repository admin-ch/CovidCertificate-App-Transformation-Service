package ch.admin.bag.covidcertificate.backend.transformation.ws.client;

import ch.admin.bag.covidcertificate.backend.transformation.model.HCertPayload;
import ch.admin.bag.covidcertificate.backend.transformation.model.renewal.BitCertRenewalPayload;
import ch.admin.bag.covidcertificate.backend.transformation.model.renewal.CertRenewalException;
import com.fasterxml.jackson.core.JsonProcessingException;

public class RenewalClient {
    private final BitClient bitClient;

    public RenewalClient(BitClient bitClient) {
        this.bitClient = bitClient;
    }

    public HCertPayload getRenewedCert(BitCertRenewalPayload payload)
            throws JsonProcessingException, CertRenewalException {
        return bitClient.getRenewedCert(payload);
    }
}
