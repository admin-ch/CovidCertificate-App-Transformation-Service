package ch.admin.bag.covidcertificate.backend.transformation.data;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

class RateLimitDataServiceTest extends BaseDataServiceTest {

    @Autowired RateLimitDataService rateLimitDataService;

    @Test
    @Transactional
    void increaseRateTest() {
        rateLimitDataService.increaseRate("uvci_1");
        rateLimitDataService.increaseRate("uvci_1");
        rateLimitDataService.increaseRate("");
        rateLimitDataService.increaseRate(null);
        assertTrue(true);
    }
}
