package ch.admin.bag.covidcertificate.backend.transformation.ws.client;

import ch.admin.bag.covidcertificate.backend.transformation.model.TestResults;
import ch.admin.bag.covidcertificate.backend.transformation.model.TestTypes;
import ch.admin.bag.covidcertificate.backend.transformation.model.VaccinationCertificateTypes;
import ch.admin.bag.covidcertificate.backend.transformation.model.pdf.BitPdfPayload;
import ch.admin.bag.covidcertificate.backend.transformation.model.cert.DecodedCert;
import ch.admin.bag.covidcertificate.backend.transformation.model.cert.DecodedRCert;
import ch.admin.bag.covidcertificate.backend.transformation.model.cert.DecodedTCert;
import ch.admin.bag.covidcertificate.backend.transformation.model.cert.DecodedVCert;
import ch.admin.bag.covidcertificate.backend.transformation.model.pdf.PdfResponse;
import ch.admin.bag.covidcertificate.backend.transformation.ws.config.model.PdfConfig;
import com.fasterxml.jackson.core.JsonProcessingException;

public class PdfClient {
    private final PdfConfig pdfConfig;
    private final BitClient bitClient;

    public PdfClient(PdfConfig pdfConfig, BitClient bitClient) {
        this.pdfConfig = pdfConfig;
        this.bitClient = bitClient;
    }

    public PdfResponse getPdf(BitPdfPayload payload) throws JsonProcessingException {
        DecodedCert decodedCert = payload.getDecodedCert();
        String endpoint;
        if (decodedCert instanceof DecodedTCert) {
            if (((DecodedTCert) decodedCert)
                    .getT()
                    .get(0)
                    .getTt()
                    .equals(TestTypes.TEST_TYPE_ANTIBODY)) {
                endpoint = pdfConfig.getAntibodyEndpoint();
            }else if(((DecodedTCert) decodedCert)
                    .getT()
                    .get(0)
                    .getTt()
                    .equals(TestTypes.TEST_TYPE_RAT) &&
                    ((DecodedTCert) decodedCert)
                            .getT()
                            .get(0)
                            .getTr()
                            .equals(TestResults.TEST_RESULT_POSITIVE)){
                endpoint = pdfConfig.getAntigenRecoveryEndpoint();
            }else if(((DecodedTCert) decodedCert)
                    .getT()
                    .get(0)
                    .getTt()
                    .equals(TestTypes.TEST_TYPE_EXEMPTION)){
                endpoint = pdfConfig.getExemptionEndpoint();
            } else {
                endpoint = pdfConfig.getTestEndpoint();
            }
        } else if (decodedCert instanceof DecodedRCert) {
            endpoint = pdfConfig.getRecoveryEndpoint();
        } else if (decodedCert instanceof DecodedVCert) {
            if (((DecodedVCert) decodedCert)
                .getV()
                .get(0)
                .getMp()
                .endsWith(VaccinationCertificateTypes.VACCINATION_CERTIFICATE_TYPE_TOURIST_SUFFIX)) {
                endpoint = pdfConfig.getVaccinationTouristEndpoint();
            } else {
                endpoint = pdfConfig.getVaccinationEndpoint();
            }
        } else {
            throw new RuntimeException(
                    "unexpected class received: " + decodedCert.getClass().getName());
        }
        return bitClient.getPdf(payload, endpoint);
    }
}
