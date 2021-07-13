package ch.admin.bag.covidcertificate.backend.transformation.ws.config;

import ch.admin.bag.covidcertificate.backend.transformation.data.RateLimitDataService;
import java.time.Duration;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT15M")
public class SchedulingConfig {

    private static final Logger logger = LoggerFactory.getLogger(SchedulingConfig.class);

    private final RateLimitDataService rateLimitDataService;

    @Value("${ws.rate-limit.retention-period:PT24H}")
    private Duration retentionPeriod;

    public SchedulingConfig(RateLimitDataService rateLimitDataService) {
        this.rateLimitDataService = rateLimitDataService;
    }

    // Run cleanjob once per hour at minute 0
    @Scheduled(cron = "${ws.rate-limit.clean-cron:0 0 * ? * *}")
    @SchedulerLock(name = "cleanDb", lockAtLeastFor = "PT15S")
    public void cleanDb() {
        logger.info("Cleaning rate-limit database entries");
        rateLimitDataService.cleanDb(retentionPeriod);
    }
}
