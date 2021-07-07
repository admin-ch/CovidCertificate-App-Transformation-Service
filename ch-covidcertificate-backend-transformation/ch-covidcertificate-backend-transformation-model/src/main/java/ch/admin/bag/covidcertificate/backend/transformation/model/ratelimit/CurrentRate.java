package ch.admin.bag.covidcertificate.backend.transformation.model.ratelimit;

public class CurrentRate {

    private String uvciHash;
    private int rate;

    public String getUvciHash() {
        return uvciHash;
    }

    public void setUvciHash(String uvciHash) {
        this.uvciHash = uvciHash;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
}
