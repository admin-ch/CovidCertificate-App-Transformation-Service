package ch.admin.bag.covidcertificate.backend.transformation.ws.util;

import ch.admin.bag.covidcertificate.backend.transformation.model.Person;
import ch.admin.bag.covidcertificate.backend.transformation.model.pdf.DecodedCert;
import ch.admin.bag.covidcertificate.backend.transformation.model.pdf.DecodedRCert;
import ch.admin.bag.covidcertificate.backend.transformation.model.pdf.DecodedTCert;
import ch.admin.bag.covidcertificate.backend.transformation.model.pdf.DecodedVCert;
import ch.admin.bag.covidcertificate.backend.transformation.model.pdf.RecoveryCert;
import ch.admin.bag.covidcertificate.backend.transformation.model.pdf.TestCert;
import ch.admin.bag.covidcertificate.backend.transformation.model.pdf.VaccinationCert;
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

    private DccHelper() {
        throw new IllegalStateException("Utility class");
    }

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

        return getFirstAndAssert(uvciList);
    }

    /**
     * Maps the given certificate to the format required for pdf export, asserting the certificate
     * only contains a single entry
     *
     * @param dccCert Decoded and verified certificate as returned by verification-check
     * @return DecodedCert
     * @throws EmptyCertificateException if the certificate doesn't contain any entries
     * @throws MultipleEntriesException if the certificate contains multiple entries
     */
    public static DecodedCert mapToDecodedCert(DccCert dccCert)
            throws EmptyCertificateException, MultipleEntriesException {
        String ver = dccCert.getVersion();
        String dob = dccCert.getDateOfBirth();
        Person nam = getPerson(dccCert);

        final List<DecodedCert> mapped = new ArrayList<>();
        if (dccCert.getVaccinations() != null) {
            mapped.addAll(
                    dccCert.getVaccinations().stream()
                            .map(
                                    v -> {
                                        VaccinationCert vCert = new VaccinationCert();
                                        vCert.setCi(v.getCertificateIdentifier());
                                        vCert.setCo(v.getCountry());
                                        vCert.setDn(v.getDoseNumber());
                                        vCert.setDt(v.getVaccinationDate());
                                        vCert.setIs(v.getCertificateIssuer());
                                        vCert.setMa(v.getMarketingAuthorizationHolder());
                                        vCert.setMp(v.getMedicinialProduct());
                                        vCert.setSd(v.getTotalDoses());
                                        vCert.setTg(v.getDisease());
                                        vCert.setVp(v.getVaccine());

                                        return new DecodedVCert(dob, nam, ver, vCert);
                                    })
                            .collect(Collectors.toList()));
        }
        if (dccCert.getTests() != null) {
            mapped.addAll(
                    dccCert.getTests().stream()
                            .map(
                                    t -> {
                                        TestCert tCert = new TestCert();
                                        tCert.setCi(t.getCertificateIdentifier());
                                        tCert.setCo(t.getCountry());
                                        tCert.setIs(t.getCertificateIssuer());
                                        tCert.setMa(t.getRatTestNameAndManufacturer());
                                        tCert.setNm(t.getNaaTestName());
                                        tCert.setSc(t.getTimestampSample());
                                        tCert.setTc(t.getTestCenter());
                                        tCert.setTg(t.getDisease());
                                        tCert.setTr(t.getResult());
                                        tCert.setTt(t.getType());

                                        return new DecodedTCert(dob, nam, ver, tCert);
                                    })
                            .collect(Collectors.toList()));
        }
        if (dccCert.getPastInfections() != null) {
            mapped.addAll(
                    dccCert.getPastInfections().stream()
                            .map(
                                    r -> {
                                        RecoveryCert rCert = new RecoveryCert();
                                        rCert.setCi(r.getCertificateIdentifier());
                                        rCert.setCo(r.getCountryOfTest());
                                        rCert.setDf(r.getValidFrom());
                                        rCert.setDu(r.getValidUntil());
                                        rCert.setFr(r.getDateFirstPositiveTest());
                                        rCert.setIs(r.getCertificateIssuer());
                                        rCert.setTg(r.getDisease());

                                        return new DecodedRCert(dob, nam, ver, rCert);
                                    })
                            .collect(Collectors.toList()));
        }

        return getFirstAndAssert(mapped);
    }

    public static Person getPerson(DccCert dccCert) {
        var name = dccCert.getPerson();
        var person = new Person();
        person.setFn(name.getFamilyName());
        person.setGn(name.getGivenName());
        person.setFnt(name.getStandardizedFamilyName());
        person.setGnt(name.getStandardizedGivenName());
        return person;
    }

    private static <T> T getFirstAndAssert(List<T> list)
            throws EmptyCertificateException, MultipleEntriesException {
        if (list.isEmpty()) {
            throw new EmptyCertificateException();
        } else if (list.size() > 1) {
            throw new MultipleEntriesException();
        } else {
            return list.get(0);
        }
    }
}
