package ch.admin.bag.covidcertificate.backend.transformation.model.renewal;

import ch.admin.bag.covidcertificate.backend.transformation.model.cert.DecodedVCert;

public class BitCertRenewalPayload {
    private final ConversionReason conversionReason = ConversionReason.VACCINATION_CONVERSION;
    private final DecodedVCert decodedCert;

    public BitCertRenewalPayload(DecodedVCert decodedCert) {
        this.decodedCert = decodedCert;
    }

    public ConversionReason getConversionReason() {
        return conversionReason;
    }

    public DecodedVCert getDecodedCert() {
        return decodedCert;
    }
}
