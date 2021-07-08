package ch.admin.bag.covidcertificate.backend.transformation.ws.controller.exceptions;

public class RateLimitExceededException extends Exception {
    private final String uvciHash;

    public RateLimitExceededException(String uvciHash) {
        this.uvciHash = uvciHash;
    }

    public String getUvciHash() {
        return uvciHash;
    }
}
