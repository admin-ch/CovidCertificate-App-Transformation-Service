package ch.admin.bag.covidcertificate.backend.transformation.ws.client;

import ch.admin.bag.covidcertificate.backend.transformation.model.lightcert.CertLightResponse;
import ch.admin.bag.covidcertificate.backend.transformation.model.HCertPayload;
import ch.admin.bag.covidcertificate.backend.transformation.model.lightcert.BitLightCertPayload;
import ch.admin.bag.covidcertificate.backend.transformation.model.pdf.BitPdfPayload;
import ch.admin.bag.covidcertificate.backend.transformation.model.pdf.PdfResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
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
    public CertLightResponse getLightCert(BitLightCertPayload payload) throws JsonProcessingException {
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
    public HCertPayload getRenewedCert() {
        // TODO replace with actual implementation
        return new HCertPayload(
                "HC1:NCFJ60EG0/3WUWGSLKH47GO0SK7KFDCBOECI9CK+500XK0JCV497F3QNFY5B3F30EN+6BY50.FK6ZK7:EDOLOPCO8F6%E3.DA%EOPC1G72A6YM8AG7EL6F:6W47F%69L6CR6%47%A8O46J%6V%60S6UPC0JCZ69FVCPD0LVC6JD846Y96*964W50S6+EDG8F3I80/D6$CBECSUER:C2$NS346$C2%E9VC- CSUE145GB8JA5B$D% D3IA4W5646946846.96.JCP9EJY8L/5M/5546.96VF63KC.SC4KCD3DX47B46IL6646H*6Z/E5JD%96IA74R6646407GVC*JCNF69463W5KF6A46TPCBEC7ZKW.C53DW3E3%E8$C JC0/D%$EY$ELPCG/DI2D/0AEZAJY8MPCG/DZUCJY8:B8O/EZKEZ967L6256V50WW6OS4Z$UEKG:.I5ANEU4C D%$CIK9U*N-F2 *40K94AJ448UZ4S4W6 P OFMH1W4M6N1MZI550*L7QHFW0C$69DXMMK1A0A.L8V48*G1:O3 A9Z33NXTJC6E5L% HXV6UBG0EU-+2:F9QCEQ3QM5LLVQC13WJ0*MQYVA 6W.+GHOFDHOQU3L9HAZR+IK5 CR9KZ9N%XERM7*Z28NG5TA8F3EQG% 7V72QJ1$I1.S3J2IZ8O3*T5DPG9NSII V8KT0YT1Z-7X%B2112BU7LAH3E.33XX3/W5D-L/%5+Q8+6L51U.2UY48OXQ LO+P9+TTPXO2PNF/II4QPXJ-E19PJP:R.4L-.3EJK7SKTU34 CSNH.CIXOBRBABOUCDUFOVMFWJUQ");
    }
}
