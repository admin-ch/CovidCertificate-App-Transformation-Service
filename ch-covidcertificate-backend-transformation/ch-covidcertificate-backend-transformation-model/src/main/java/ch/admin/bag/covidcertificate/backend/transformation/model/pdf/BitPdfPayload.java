package ch.admin.bag.covidcertificate.backend.transformation.model.pdf;

import ch.admin.bag.covidcertificate.backend.transformation.model.cert.DecodedCert;
import java.time.Instant;

public class BitPdfPayload {
    private Long issuedAt;
    private String hcert;
    private DecodedCert decodedCert;
    private String language;

    public Long getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Instant issuedAt) {
        this.issuedAt = issuedAt.toEpochMilli();
    }

    public void setIssuedAt(Long issuedAt) {
        this.issuedAt = issuedAt;
    }

    public String getHcert() {
        return hcert;
    }

    public void setHcert(String hcert) {
        this.hcert = hcert;
    }

    public DecodedCert getDecodedCert() {
        return decodedCert;
    }

    public void setDecodedCert(DecodedCert decodedCert) {
        this.decodedCert = decodedCert;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        if (language == null) {
            language = Language.getFallback();
        }
        this.language = language.name().toLowerCase();
    }
}
