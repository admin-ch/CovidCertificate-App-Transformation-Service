package ch.admin.bag.covidcertificate.backend.transformation.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.admin.bag.covidcertificate.backend.transformation.model.TransformationType;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

class RateLimitDataServiceTest extends BaseDataServiceTest {

    @Autowired RateLimitDataService rateLimitDataService;

    @Test
    @Transactional
    void addTransformationLogTest() {
        final String uvci1 = "uvci_1";
        rateLimitDataService.addTransformationLog(uvci1, TransformationType.LIGHT_CERT);
        rateLimitDataService.addTransformationLog(uvci1, TransformationType.LIGHT_CERT);
        rateLimitDataService.addTransformationLog("", TransformationType.LIGHT_CERT);
        rateLimitDataService.addTransformationLog(null, TransformationType.LIGHT_CERT);
        assertEquals(
                2,
                rateLimitDataService.getTransformationCount(uvci1, TransformationType.LIGHT_CERT));
        assertEquals(0, rateLimitDataService.getTransformationCount(uvci1, TransformationType.PDF));
    }

    @Test
    @Transactional
    void getTransformationCountTest() {
        final String uvci1 = "uvci_1";
        final String uvci2 = "uvci_2";
        final String uvci3 = "uvci_3";
        rateLimitDataService.addTransformationLog(uvci1, TransformationType.LIGHT_CERT);
        rateLimitDataService.addTransformationLog(uvci2, TransformationType.LIGHT_CERT);
        rateLimitDataService.addTransformationLog(uvci1, TransformationType.LIGHT_CERT);
        assertEquals(
                2,
                rateLimitDataService.getTransformationCount(uvci1, TransformationType.LIGHT_CERT));
        assertEquals(
                1,
                rateLimitDataService.getTransformationCount(uvci2, TransformationType.LIGHT_CERT));
        assertEquals(
                0,
                rateLimitDataService.getTransformationCount(uvci3, TransformationType.LIGHT_CERT));
    }

    @Test
    @Transactional
    void removeOldLogsTest() throws Exception {
        final String uvci1 = "uvci_1";
        final String uvci2 = "uvci_2";
        final String uvci3 = "uvci_3";
        rateLimitDataService.addTransformationLog(uvci1, TransformationType.LIGHT_CERT);
        rateLimitDataService.addTransformationLog(uvci1, TransformationType.PDF);
        rateLimitDataService.addTransformationLog(uvci1, TransformationType.PDF);
        rateLimitDataService.addTransformationLog(uvci2, TransformationType.LIGHT_CERT);
        assertEquals(
                1,
                rateLimitDataService.getTransformationCount(uvci1, TransformationType.LIGHT_CERT));
        assertEquals(2, rateLimitDataService.getTransformationCount(uvci1, TransformationType.PDF));
        assertEquals(
                1,
                rateLimitDataService.getTransformationCount(uvci2, TransformationType.LIGHT_CERT));
        assertEquals(
                0,
                rateLimitDataService.getTransformationCount(uvci3, TransformationType.LIGHT_CERT));
        TimeUnit.SECONDS.sleep(1);
        rateLimitDataService.removeOldLogs(Duration.ZERO);
        assertEquals(
                0,
                rateLimitDataService.getTransformationCount(uvci1, TransformationType.LIGHT_CERT));
        assertEquals(
                0,
                rateLimitDataService.getTransformationCount(uvci2, TransformationType.LIGHT_CERT));
        assertEquals(
                0,
                rateLimitDataService.getTransformationCount(uvci3, TransformationType.LIGHT_CERT));
    }
}
