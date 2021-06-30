package ch.admin.bag.covidcertificate.backend.transformation.model;

import ch.ubique.openapi.docannotations.Documentation;
import javax.validation.constraints.NotNull;

public class CertLightPayload {

    @Documentation(
            description = "Base-45-encoded light certificate with prefix LT1:",
            example = "LT1:6BFO9...PYT5R:8")
    @NotNull
    private final String payload;

    @Documentation(description = "Base-64-encoded png image of the qr-code")
    @NotNull
    private final String qrcode;

    public CertLightPayload(String payload, String qrcode) {
        this.payload = payload;
        this.qrcode = qrcode;
    }

    public String getPayload() {
        return payload;
    }

    public String getQrcode() {
        return qrcode;
    }
}
