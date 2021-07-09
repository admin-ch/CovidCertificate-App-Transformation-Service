package ch.admin.bag.covidcertificate.backend.transformation.ws.service;

import ch.admin.bag.covidcertificate.backend.transformation.data.RateLimitDataService;
import ch.admin.bag.covidcertificate.backend.transformation.ws.controller.exceptions.EmptyCertificateException;
import ch.admin.bag.covidcertificate.backend.transformation.ws.controller.exceptions.RateLimitExceededException;
import ch.admin.bag.covidcertificate.sdk.core.models.healthcert.eu.DccCert;
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

    public void checkRateLimit(DccCert euCert)
            throws EmptyCertificateException, NoSuchAlgorithmException, RateLimitExceededException {
        final String uvci;
        if (euCert.getVaccinations() != null && !euCert.getVaccinations().isEmpty()) {
            uvci = euCert.getVaccinations().get(0).getCertificateIdentifier();
        } else if (euCert.getTests() != null && !euCert.getTests().isEmpty()) {
            uvci = euCert.getTests().get(0).getCertificateIdentifier();
        } else if (euCert.getPastInfections() != null && !euCert.getPastInfections().isEmpty()) {
            uvci = euCert.getPastInfections().get(0).getCertificateIdentifier();
        } else {
            throw new EmptyCertificateException();
        }

        final var digest = MessageDigest.getInstance("SHA-256");
        final var uvciHash = Base64.getEncoder().encodeToString(digest.digest(uvci.getBytes()));

        final int count = rateLimitDataService.getCurrentRate(uvciHash);
        if (count < rateLimit) {
            rateLimitDataService.increaseRate(uvciHash);
        } else {
            throw new RateLimitExceededException(uvciHash);
        }
    }
}
