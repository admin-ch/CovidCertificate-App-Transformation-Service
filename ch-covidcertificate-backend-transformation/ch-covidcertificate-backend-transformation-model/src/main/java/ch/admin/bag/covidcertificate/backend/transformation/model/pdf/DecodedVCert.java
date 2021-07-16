package ch.admin.bag.covidcertificate.backend.transformation.model.pdf;

import ch.admin.bag.covidcertificate.backend.transformation.model.Person;
import java.util.List;

public class DecodedVCert extends DecodedCert {
    private List<VaccinationCert> v;

    public DecodedVCert(String dob, Person nam, String ver, VaccinationCert v) {
        super(dob, nam, ver);
        this.v = List.of(v);
    }

    public List<VaccinationCert> getV() {
        return v;
    }
}
