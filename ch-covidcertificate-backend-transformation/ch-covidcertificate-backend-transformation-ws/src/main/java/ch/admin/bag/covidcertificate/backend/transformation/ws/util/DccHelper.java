package ch.admin.bag.covidcertificate.backend.transformation.ws.util;

import ch.admin.bag.covidcertificate.backend.transformation.ws.client.exceptions.EmptyCertificateException;
import ch.admin.bag.covidcertificate.backend.transformation.ws.client.exceptions.MultipleEntriesException;
import ch.admin.bag.covidcertificate.sdk.core.models.healthcert.eu.DccCert;
import ch.admin.bag.covidcertificate.sdk.core.models.healthcert.eu.RecoveryEntry;
import ch.admin.bag.covidcertificate.sdk.core.models.healthcert.eu.TestEntry;
import ch.admin.bag.covidcertificate.sdk.core.models.healthcert.eu.VaccinationEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DccHelper {

    /**
     * Fetches the UVCI of the given certificate, asserting the certificate only contains a single
     * entry
     *
     * @param dccCert Decoded and verified certificate as returned by verification-check
     * @return UVCI
     * @throws EmptyCertificateException if the certificate doesn't contain any entries
     * @throws MultipleEntriesException if the certificate contains multiple entries
     */
    public static String getUvci(DccCert dccCert)
            throws EmptyCertificateException, MultipleEntriesException {
        final List<String> uvciList = new ArrayList<>();
        if (dccCert.getVaccinations() != null && !dccCert.getVaccinations().isEmpty()) {
            uvciList.addAll(
                    dccCert.getVaccinations().stream()
                            .map(VaccinationEntry::getCertificateIdentifier)
                            .collect(Collectors.toList()));
        }
        if (dccCert.getTests() != null && !dccCert.getTests().isEmpty()) {
            uvciList.addAll(
                    dccCert.getTests().stream()
                            .map(TestEntry::getCertificateIdentifier)
                            .collect(Collectors.toList()));
        }
        if (dccCert.getPastInfections() != null && !dccCert.getPastInfections().isEmpty()) {
            uvciList.addAll(
                    dccCert.getPastInfections().stream()
                            .map(RecoveryEntry::getCertificateIdentifier)
                            .collect(Collectors.toList()));
        }

        if (uvciList.isEmpty()) {
            throw new EmptyCertificateException();
        } else if (uvciList.size() > 1) {
            throw new MultipleEntriesException();
        } else {
            return uvciList.get(0);
        }
    }
}
