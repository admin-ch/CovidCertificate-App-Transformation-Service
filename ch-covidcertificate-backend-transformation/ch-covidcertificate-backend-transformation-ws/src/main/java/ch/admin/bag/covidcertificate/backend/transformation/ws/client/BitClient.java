package ch.admin.bag.covidcertificate.backend.transformation.ws.client;

import ch.admin.bag.covidcertificate.backend.transformation.model.HCertPayload;
import ch.admin.bag.covidcertificate.backend.transformation.model.lightcert.BitLightCertPayload;
import ch.admin.bag.covidcertificate.backend.transformation.model.lightcert.CertLightResponse;
import ch.admin.bag.covidcertificate.backend.transformation.model.pdf.BitPdfPayload;
import ch.admin.bag.covidcertificate.backend.transformation.model.pdf.PdfResponse;
import ch.admin.bag.covidcertificate.backend.transformation.model.renewal.BitCertRenewalPayload;
import ch.admin.bag.covidcertificate.backend.transformation.model.renewal.CertRenewalException;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface BitClient {
    public CertLightResponse getLightCert(BitLightCertPayload payload)
            throws JsonProcessingException;

    public PdfResponse getPdf(BitPdfPayload payload, String endpoint)
            throws JsonProcessingException;

    public HCertPayload getRenewedCert(BitCertRenewalPayload payload)
            throws JsonProcessingException, CertRenewalException;
}
