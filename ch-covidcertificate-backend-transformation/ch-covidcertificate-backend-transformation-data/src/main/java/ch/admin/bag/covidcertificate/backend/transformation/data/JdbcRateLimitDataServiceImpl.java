package ch.admin.bag.covidcertificate.backend.transformation.data;

import ch.admin.bag.covidcertificate.backend.transformation.model.ratelimit.CurrentRate;
import java.time.Duration;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

public class JdbcRateLimitDataServiceImpl implements RateLimitDataService {

    private static final Logger logger =
            LoggerFactory.getLogger(JdbcRateLimitDataServiceImpl.class);

    private final NamedParameterJdbcTemplate jt;
    private final SimpleJdbcInsert rateLimitInsert;

    public JdbcRateLimitDataServiceImpl(DataSource dataSource) {
        this.jt = new NamedParameterJdbcTemplate(dataSource);
        this.rateLimitInsert =
                new SimpleJdbcInsert(dataSource)
                        .withTableName("t_rate_limit")
                        .usingGeneratedKeyColumns("pk_rate_limit", "created_at");
    }

    @Override
    public CurrentRate getCurrentRate(String uvciHash) {
        return null;
    }

    @Override
    public void increaseRate(String uvciHash) {
        logger.debug("Adding entry for uvci hash: {}", uvciHash);
        if (uvciHash != null && !uvciHash.isBlank()) {
            var params = new MapSqlParameterSource("uvci_hash", uvciHash);
            rateLimitInsert.execute(params);
        }
    }

    @Override
    public int cleanDB(Duration retentionperiod) {
        return 0;
    }
}
