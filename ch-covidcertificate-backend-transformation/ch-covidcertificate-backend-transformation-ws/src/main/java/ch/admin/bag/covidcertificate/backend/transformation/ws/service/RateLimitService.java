package ch.admin.bag.covidcertificate.backend.transformation.ws.service;

import ch.admin.bag.covidcertificate.backend.transformation.data.RateLimitDataService;
import ch.admin.bag.covidcertificate.backend.transformation.ws.client.exceptions.RateLimitExceededException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class RateLimitService {

    private final RateLimitDataService rateLimitDataService;
    private final int rateLimit;

    public RateLimitService(RateLimitDataService rateLimitDataService, int rateLimit) {
        this.rateLimitDataService = rateLimitDataService;
        this.rateLimit = rateLimit;
    }

    /**
     * Checks that the rate limit hasn't been exceeded within a rolling time window (default: 24
     * hours) and increases it if it hasn't.
     *
     * @param uvci identifier to check current rate for
     * @throws NoSuchAlgorithmException If hashing algorithm isn't supported
     * @throws RateLimitExceededException If rate limit is exceeded for the given identifier
     */
    public void checkRateLimit(String uvci)
            throws NoSuchAlgorithmException, RateLimitExceededException {
        final String uvciHash = getSha256Hash(uvci);
        if (rateLimitDataService.getCurrentRate(uvciHash) >= rateLimit) {
            throw new RateLimitExceededException(uvciHash);
        }
    }

    public void updateCount(String uvci) throws NoSuchAlgorithmException {
        rateLimitDataService.increaseRate(getSha256Hash(uvci));
    }

    private String getSha256Hash(String toHash) throws NoSuchAlgorithmException {
        final var digest = MessageDigest.getInstance("SHA-256");
        return Base64.getEncoder().encodeToString(digest.digest(toHash.getBytes()));
    }
}
