package ch.admin.bag.covidcertificate.backend.transformation.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
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
        assertTrue(rateLimitDataService.getCurrentRate("uvci_1") > 0);
    }

    @Test
    @Transactional
    void getCurrentRateTest() {
        rateLimitDataService.increaseRate("uvci_1");
        rateLimitDataService.increaseRate("uvci_2");
        rateLimitDataService.increaseRate("uvci_1");
        assertEquals(2, rateLimitDataService.getCurrentRate("uvci_1"));
        assertEquals(1, rateLimitDataService.getCurrentRate("uvci_2"));
        assertEquals(0, rateLimitDataService.getCurrentRate("uvci_3"));
    }

    @Test
    @Transactional
    void cleanDBTest() {
        rateLimitDataService.increaseRate("uvci_1");
        rateLimitDataService.increaseRate("uvci_2");
        assertTrue(rateLimitDataService.getCurrentRate("uvci_1") > 0);
        assertTrue(rateLimitDataService.getCurrentRate("uvci_2") > 0);
        assertEquals(0, rateLimitDataService.getCurrentRate("uvci_3"));
        rateLimitDataService.cleanDB(Duration.ZERO);
        assertEquals(0, rateLimitDataService.getCurrentRate("uvci_1"));
        assertEquals(0, rateLimitDataService.getCurrentRate("uvci_2"));
        assertEquals(0, rateLimitDataService.getCurrentRate("uvci_3"));
    }
}
