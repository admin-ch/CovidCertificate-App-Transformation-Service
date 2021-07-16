package ch.admin.bag.covidcertificate.backend.transformation.ws.client;

import ch.admin.bag.covidcertificate.backend.transformation.model.CertLightResponse;
import ch.admin.bag.covidcertificate.backend.transformation.model.TransformPayload;
import ch.admin.bag.covidcertificate.backend.transformation.model.pdf.BitPdfPayload;
import ch.admin.bag.covidcertificate.backend.transformation.model.pdf.PdfResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedInputStream;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

public class BitClientMock implements BitClient {

    private static final Logger logger = LoggerFactory.getLogger(BitClientMock.class);

    private static final String LIGHT_CERT_MOCK = "dev/light-cert-mock.json";
    private final CertLightResponse certLightMock;

    private static final String PDF_MOCK = "dev/cert-pdf-mock.pdf";
    private final PdfResponse pdfMock;

    public BitClientMock(ObjectMapper objectMapper) {
        try (var is = new ClassPathResource(PDF_MOCK).getInputStream();
                var bis = new BufferedInputStream(is)) {
            final byte[] pdfBytes = bis.readAllBytes();
            final var pdfEncoded = Base64.getEncoder().encodeToString(pdfBytes);
            pdfMock = new PdfResponse(pdfEncoded);
        } catch (Exception e) {
            String msg = "loading pdf mock failed";
            logger.error(msg, e);
            throw new RuntimeException(msg);
        }

        try (var is = new ClassPathResource(LIGHT_CERT_MOCK).getInputStream()) {
            certLightMock = objectMapper.readValue(is, CertLightResponse.class);
        } catch (Exception e) {
            String msg = "loading light cert mock failed";
            logger.error(msg, e);
            throw new RuntimeException(msg);
        }
    }

    @Override
    public CertLightResponse getLightCert(TransformPayload payload) {
        return certLightMock;
    }

    @Override
    public PdfResponse getPdf(BitPdfPayload payload, String endpoint) {
        return pdfMock;
    }
}
