package ch.admin.bag.covidcertificate.backend.transformation.ws.client;

import ch.admin.bag.covidcertificate.backend.transformation.model.CertLightResponse;
import ch.admin.bag.covidcertificate.backend.transformation.model.HCertPayload;
import ch.admin.bag.covidcertificate.backend.transformation.model.TransformPayload;
import ch.admin.bag.covidcertificate.backend.transformation.model.pdf.BitPdfPayload;
import ch.admin.bag.covidcertificate.backend.transformation.model.pdf.PdfResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface BitClient {
    public CertLightResponse getLightCert(TransformPayload payload) throws JsonProcessingException;

    public PdfResponse getPdf(BitPdfPayload payload, String endpoint)
            throws JsonProcessingException;

    public HCertPayload getRenewedCert();
}
