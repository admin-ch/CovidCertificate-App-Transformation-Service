package ch.admin.bag.covidcertificate.backend.transformation.model.pdf;

import ch.admin.bag.covidcertificate.backend.transformation.model.Person;

public abstract class DecodedCert {

    protected String dob;
    protected Person nam;
    protected String ver;

    protected DecodedCert(String dob, Person nam, String ver) {
        this.dob = dob;
        this.nam = nam;
        this.ver = ver;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public Person getNam() {
        return nam;
    }

    public void setNam(Person nam) {
        this.nam = nam;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }
}
