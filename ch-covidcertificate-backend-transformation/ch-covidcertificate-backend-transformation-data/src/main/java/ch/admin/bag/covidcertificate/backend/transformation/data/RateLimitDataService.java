package ch.admin.bag.covidcertificate.backend.transformation.data;

import ch.admin.bag.covidcertificate.backend.transformation.model.TransformationType;
import java.time.Duration;

public interface RateLimitDataService {

    /**
     * Gets the current number of successful transformations for the given uvciHash and type
     *
     * @param uvciHash string for which to count the number of entries
     * @param type transformation type
     * @return number of successful transformations
     */
    public int getTransformationCount(String uvciHash, TransformationType type);

    /**
     * Adds an entry to the database to log a successful transformation
     *
     * @param uvciHash non-empty identifier to use in the new entry
     * @param type transformation type
     */
    public void addTransformationLog(String uvciHash, TransformationType type);

    /**
     * Removes all transformation logs older than the given retentionPeriod
     *
     * @param retentionPeriod Duration indicating the amount of time a log entry should be kept
     * @return number of removed entries
     */
    public int removeOldLogs(Duration retentionPeriod);
}
