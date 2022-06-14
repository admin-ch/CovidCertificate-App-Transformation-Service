package ch.admin.bag.covidcertificate.backend.transformation.ws.client;

import ch.admin.bag.covidcertificate.backend.transformation.model.HCertPayload;
import ch.admin.bag.covidcertificate.backend.transformation.model.lightcert.BitLightCertPayload;
import ch.admin.bag.covidcertificate.backend.transformation.model.lightcert.CertLightResponse;
import ch.admin.bag.covidcertificate.backend.transformation.model.pdf.BitPdfPayload;
import ch.admin.bag.covidcertificate.backend.transformation.model.pdf.PdfResponse;
import ch.admin.bag.covidcertificate.backend.transformation.model.renewal.BitCertRenewalErrorResponse;
import ch.admin.bag.covidcertificate.backend.transformation.model.renewal.BitCertRenewalPayload;
import ch.admin.bag.covidcertificate.backend.transformation.model.renewal.BitCertRenewalResponse;
import ch.admin.bag.covidcertificate.backend.transformation.model.renewal.CertRenewalException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

public class BitClientOauthImpl implements BitClient {
    private static final Logger logger = LoggerFactory.getLogger(BitClientOauthImpl.class);

    private final String lightCertificateEndpoint;
    private final String certRenewalEndpoint;
    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    public BitClientOauthImpl(
            String clientId,
            ClientRegistrationRepository clientRegistrations,
            String lightCertificateEndpoint,
            ObjectMapper objectMapper,
            String certRenewalEndpoint) {
        this.lightCertificateEndpoint = lightCertificateEndpoint;
        this.certRenewalEndpoint = certRenewalEndpoint;
        this.objectMapper = objectMapper;

        var clientRepository =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                        clientRegistrations,
                        new InMemoryOAuth2AuthorizedClientService(clientRegistrations));
        var oauth = new ServletOAuth2AuthorizedClientExchangeFilterFunction(clientRepository);
        oauth.setDefaultClientRegistrationId(clientId);
        this.webClient =
                WebClient.builder()
                        .filter(oauth)
                        .exchangeStrategies(
                                ExchangeStrategies.builder()
                                        .codecs(
                                                configurer ->
                                                        configurer
                                                                .defaultCodecs()
                                                                .maxInMemorySize(
                                                                        20 * 1024 * 1024)) // 20MB
                                        .build())
                        .build();
    }

    @Override
    public CertLightResponse getLightCert(BitLightCertPayload payload)
            throws JsonProcessingException {
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

    @Override
    public HCertPayload getRenewedCert(BitCertRenewalPayload payload)
            throws JsonProcessingException, CertRenewalException {
        try {
            var responseStr =
                    webClient
                            .post()
                            .uri(certRenewalEndpoint)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .bodyValue(payload)
                            .retrieve()
                            .bodyToMono(String.class)
                            .block();
            final BitCertRenewalResponse response =
                    objectMapper.readValue(responseStr, BitCertRenewalResponse.class);
            return new HCertPayload(response.getPayload());
        } catch (WebClientResponseException e) {
            try {
                final BitCertRenewalErrorResponse errorResponse =
                        objectMapper.readValue(
                                e.getResponseBodyAsString(), BitCertRenewalErrorResponse.class);
                throw new CertRenewalException(errorResponse);
            } catch (JsonProcessingException ex) {
                logger.warn("couldn't parse error response: {}", e.getResponseBodyAsString());
                throw new CertRenewalException();
            }
        }
    }
}
