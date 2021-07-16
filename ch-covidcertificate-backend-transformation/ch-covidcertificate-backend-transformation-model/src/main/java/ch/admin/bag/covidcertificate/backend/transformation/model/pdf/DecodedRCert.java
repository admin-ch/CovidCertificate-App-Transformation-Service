package ch.admin.bag.covidcertificate.backend.transformation.model.pdf;

import ch.admin.bag.covidcertificate.backend.transformation.model.Person;
import java.util.List;

public class DecodedRCert extends DecodedCert {
    private List<RecoveryCert> r;

    public DecodedRCert(String dob, Person nam, String ver, RecoveryCert r) {
        super(dob, nam, ver);
        this.r = List.of(r);
    }

    public List<RecoveryCert> getR() {
        return r;
    }
}
