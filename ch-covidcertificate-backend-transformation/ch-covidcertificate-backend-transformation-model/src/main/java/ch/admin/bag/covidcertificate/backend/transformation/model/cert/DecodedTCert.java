package ch.admin.bag.covidcertificate.backend.transformation.model.cert;

import ch.admin.bag.covidcertificate.backend.transformation.model.Person;
import java.util.List;

public class DecodedTCert extends DecodedCert {
    private List<TestCert> t;

    public DecodedTCert(String dob, Person nam, String ver, TestCert t) {
        super(dob, nam, ver);
        this.t = List.of(t);
    }

    public List<TestCert> getT() {
        return t;
    }
}
