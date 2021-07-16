package ch.admin.bag.covidcertificate.backend.transformation.ws.client;

import ch.admin.bag.covidcertificate.backend.transformation.model.CertLightResponse;
import ch.admin.bag.covidcertificate.backend.transformation.model.TransformPayload;
import ch.admin.bag.covidcertificate.backend.transformation.model.pdf.BitPdfPayload;
import ch.admin.bag.covidcertificate.backend.transformation.model.pdf.PdfResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

public class BitClientOauthImpl implements BitClient {
    private final String lightCertificateEndpoint;
    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    public BitClientOauthImpl(
            String clientId,
            ClientRegistrationRepository clientRegistrations,
            String lightCertificateEndpoint,
            ObjectMapper objectMapper) {
        this.lightCertificateEndpoint = lightCertificateEndpoint;
        this.objectMapper = objectMapper;

        var clientRepository =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                        clientRegistrations,
                        new InMemoryOAuth2AuthorizedClientService(clientRegistrations));
        var oauth = new ServletOAuth2AuthorizedClientExchangeFilterFunction(clientRepository);
        oauth.setDefaultClientRegistrationId(clientId);
        this.webClient = WebClient.builder().filter(oauth).build();
    }

    @Override
    public CertLightResponse getLightCert(TransformPayload payload) throws JsonProcessingException {
        var response =
                webClient
                        .post()
                        .uri(lightCertificateEndpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .bodyValue(payload)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
        return objectMapper.readValue(response, CertLightResponse.class);
    }

    @Override
    public PdfResponse getPdf(BitPdfPayload payload, String endpoint)
            throws JsonProcessingException {
        var response =
                webClient
                        .post()
                        .uri(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .bodyValue(payload)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
        return objectMapper.readValue(response, PdfResponse.class);
    }
}
