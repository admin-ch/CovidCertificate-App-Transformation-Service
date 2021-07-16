package ch.admin.bag.covidcertificate.backend.transformation.ws.client;

import ch.admin.bag.covidcertificate.backend.transformation.model.pdf.BitPdfPayload;
import ch.admin.bag.covidcertificate.backend.transformation.model.pdf.DecodedCert;
import ch.admin.bag.covidcertificate.backend.transformation.model.pdf.DecodedRCert;
import ch.admin.bag.covidcertificate.backend.transformation.model.pdf.DecodedTCert;
import ch.admin.bag.covidcertificate.backend.transformation.model.pdf.DecodedVCert;
import ch.admin.bag.covidcertificate.backend.transformation.model.pdf.PdfResponse;
import ch.admin.bag.covidcertificate.backend.transformation.ws.config.model.PdfConfig;
import ch.admin.bag.covidcertificate.backend.transformation.ws.util.OauthWebClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

public class PdfClient {
    private final PdfConfig pdfConfig;
    private final OauthWebClient oauthWebClient;
    private final ObjectMapper objectMapper;

    public PdfClient(PdfConfig pdfConfig, OauthWebClient oauthWebClient) {
        this.pdfConfig = pdfConfig;
        this.oauthWebClient = oauthWebClient;
        this.objectMapper = new ObjectMapper();
    }

    public PdfResponse getPdf(BitPdfPayload payload) throws JsonProcessingException {
        DecodedCert decodedCert = payload.getDecodedCert();
        String endpoint;
        if (decodedCert instanceof DecodedTCert) {
            endpoint = pdfConfig.getTestEndpoint();
        } else if (decodedCert instanceof DecodedRCert) {
            endpoint = pdfConfig.getRecoveryEndpoint();
        } else if (decodedCert instanceof DecodedVCert) {
            endpoint = pdfConfig.getVaccinationEndpoint();
        } else {
            throw new RuntimeException(
                    "unexpected class received: " + decodedCert.getClass().getName());
        }
        var response =
                oauthWebClient
                        .getWebClient()
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
