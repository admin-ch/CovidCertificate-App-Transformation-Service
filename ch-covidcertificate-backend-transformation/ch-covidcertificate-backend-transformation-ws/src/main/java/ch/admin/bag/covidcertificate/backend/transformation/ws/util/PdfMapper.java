package ch.admin.bag.covidcertificate.backend.transformation.ws.util;

import ch.admin.bag.covidcertificate.backend.transformation.model.pdf.BitPdfPayload;
import ch.admin.bag.covidcertificate.backend.transformation.model.pdf.Language;
import ch.admin.bag.covidcertificate.backend.transformation.ws.client.exceptions.EmptyCertificateException;
import ch.admin.bag.covidcertificate.backend.transformation.ws.client.exceptions.MultipleEntriesException;
import ch.admin.bag.covidcertificate.sdk.core.models.healthcert.CertificateHolder;
import ch.admin.bag.covidcertificate.sdk.core.models.healthcert.eu.DccCert;

public class PdfMapper {
    private PdfMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static BitPdfPayload mapToBitPayload(
            CertificateHolder certificateHolder, Language language)
            throws EmptyCertificateException, MultipleEntriesException {
        var mapped = new BitPdfPayload();
        mapped.setIssuedAt(certificateHolder.getIssuedAt());
        mapped.setHcert(certificateHolder.getQrCodeData());
        mapped.setDecodedCert(
                DccHelper.mapToDecodedCert((DccCert) certificateHolder.getCertificate()));
        mapped.setLanguage(language);
        return mapped;
    }
}
