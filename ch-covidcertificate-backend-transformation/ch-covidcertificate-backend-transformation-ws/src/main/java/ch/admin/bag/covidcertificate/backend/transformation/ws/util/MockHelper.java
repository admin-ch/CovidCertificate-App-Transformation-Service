package ch.admin.bag.covidcertificate.backend.transformation.ws.util;

import ch.admin.bag.covidcertificate.backend.transformation.model.CertLightPayload;
import ch.admin.bag.covidcertificate.backend.transformation.model.HCertPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockHelper {

    private static final Logger logger = LoggerFactory.getLogger(MockHelper.class);
    private final String mockUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public MockHelper(String mockUrl) {
        this.mockUrl = mockUrl;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public CertLightPayload getCertLightMock(HCertPayload hCertPayload)
            throws IOException, URISyntaxException, InterruptedException {
        logger.info("Sending POST request to mock endpoint");
        final String hCert = objectMapper.writeValueAsString(hCertPayload);
        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(new URI(mockUrl))
                        .POST(BodyPublishers.ofString(hCert))
                        .build();
        final HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), CertLightPayload.class);
    }
}
