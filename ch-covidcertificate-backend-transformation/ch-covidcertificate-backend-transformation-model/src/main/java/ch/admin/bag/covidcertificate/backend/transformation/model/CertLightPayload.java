package ch.admin.bag.covidcertificate.backend.transformation.model;

import ch.ubique.openapi.docannotations.Documentation;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonAlias;

public class CertLightPayload {

    @Documentation(
            description = "Base-45-encoded light certificate with prefix LT1:",
            example = "LT1:6BFO9...PYT5R:8")
    @NotNull
    private String payload;

    @Documentation(description = "Base-64-encoded png image of the qr-code")
    @NotNull
    @JsonAlias({"qrCode", "qrcode"})
    private String qrcode;

    public String getPayload() {
        return payload;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }
}
