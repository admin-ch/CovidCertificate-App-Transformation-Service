package ch.admin.bag.covidcertificate.backend.transformation.model.renewal;

public class BitCertRenewalResponse {
    private String payload;
    private String uvci;

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getUvci() {
        return uvci;
    }

    public void setUvci(String uvci) {
        this.uvci = uvci;
    }
}
