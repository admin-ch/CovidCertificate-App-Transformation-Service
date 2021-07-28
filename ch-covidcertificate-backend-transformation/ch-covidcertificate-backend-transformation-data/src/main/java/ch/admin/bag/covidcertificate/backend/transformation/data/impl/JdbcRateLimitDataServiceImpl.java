package ch.admin.bag.covidcertificate.backend.transformation.data.impl;

import ch.admin.bag.covidcertificate.backend.transformation.data.RateLimitDataService;
import ch.admin.bag.covidcertificate.backend.transformation.model.TransformationType;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.transaction.annotation.Transactional;

public class JdbcRateLimitDataServiceImpl implements RateLimitDataService {

    private static final Logger logger =
            LoggerFactory.getLogger(JdbcRateLimitDataServiceImpl.class);

    private final NamedParameterJdbcTemplate jt;
    private final SimpleJdbcInsert rateLimitInsert;

    public JdbcRateLimitDataServiceImpl(DataSource dataSource) {
        this.jt = new NamedParameterJdbcTemplate(dataSource);
        this.rateLimitInsert =
                new SimpleJdbcInsert(dataSource)
                        .withTableName("t_transformation_log")
                        .usingGeneratedKeyColumns("pk_transformation_log_id", "created_at");
    }

    @Override
    @Transactional(readOnly = true)
    public int getTransformationCount(String uvciHash, TransformationType type) {
        logger.debug("Fetching current count for type {} and uvci hash {}", type, uvciHash);
        final var getCountSql =
                "select count(1) from t_transformation_log"
                        + " where uvci_hash = :uvci_hash"
                        + " and type = :type";
        return jt.queryForObject(getCountSql, createParams(uvciHash, type), Integer.class);
    }

    @Override
    @Transactional(readOnly = false)
    public void addTransformationLog(String uvciHash, TransformationType type) {
        logger.debug("Adding log for type {} and uvci hash: {}", type, uvciHash);
        if (uvciHash != null && !uvciHash.isBlank()) {
            rateLimitInsert.execute(createParams(uvciHash, type));
        }
    }

    private MapSqlParameterSource createParams(String uvciHash, TransformationType type) {
        var params = new MapSqlParameterSource();
        params.addValue("uvci_hash", uvciHash);
        params.addValue("type", type.name());
        return params;
    }

    @Override
    @Transactional(readOnly = false)
    public int removeOldLogs(Duration retentionPeriod) {
        var retentionTime = Date.from(Instant.now().minus(retentionPeriod));
        logger.debug("Removing entries before {}", retentionTime);
        var params = new MapSqlParameterSource("retention_time", retentionTime);
        var cleanupSql = "delete from t_transformation_log where created_at < :retention_time";
        return jt.update(cleanupSql, params);
    }
}
