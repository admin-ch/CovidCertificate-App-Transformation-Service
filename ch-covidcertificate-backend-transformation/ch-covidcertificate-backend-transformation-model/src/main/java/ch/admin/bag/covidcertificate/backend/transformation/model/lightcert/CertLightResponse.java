package ch.admin.bag.covidcertificate.backend.transformation.model.lightcert;

import ch.ubique.openapi.docannotations.Documentation;
import com.fasterxml.jackson.annotation.JsonAlias;
import javax.validation.constraints.NotNull;

public class CertLightResponse {

    @Documentation(
            description = "Base-45-encoded light certificate with prefix LT1:",
            example = "LT1:6BFO9...PYT5R:8")
    @NotNull
    private String payload;

    @Documentation(description = "Base-64-encoded png image of the qr-code")
    @NotNull
    @JsonAlias({"qrCode", "qrcode"})
    private String qrCode;

    public String getPayload() {
        return payload;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}
