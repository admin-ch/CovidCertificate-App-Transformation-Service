package ch.admin.bag.covidcertificate.backend.transformation.data;

import java.time.Duration;

public interface RateLimitDataService {

    /**
     * Gets the current number of entries in the database for the given uvciHash
     *
     * @param uvciHash string for which to count the number of entries
     * @return number of entries
     */
    public int getCurrentRate(String uvciHash);

    /**
     * Adds an entry to the database to indicate a rate-increase
     *
     * @param uvciHash non-empty identifier to use in the new entry
     */
    public void increaseRate(String uvciHash);

    /**
     * Removes all entries in the current database older than the given retentionPeriod
     *
     * @param retentionPeriod Duration indicating the amount of time an entry should be kept
     * @return number of removed entries
     */
    public int cleanDb(Duration retentionPeriod);
}
