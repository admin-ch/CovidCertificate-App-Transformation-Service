package ch.admin.bag.covidcertificate.backend.transformation.model.pdf;

/**
 * This is a java implementation of the `vaccination_entry` json spec found at
 * https://github.com/ehn-dcc-development/ehn-dcc-schema/blob/1.0.0/DGC.combined-schema.json
 */
public class VaccinationCert {
    private String ci;
    private String co;
    private Integer dn;
    private String dt;
    private String is;
    private String ma;
    private String mp;
    private Integer sd;
    private String tg;
    private String vp;

    public String getCi() {
        return ci;
    }

    public void setCi(String ci) {
        this.ci = ci;
    }

    public String getCo() {
        return co;
    }

    public void setCo(String co) {
        this.co = co;
    }

    public Integer getDn() {
        return dn;
    }

    public void setDn(Integer dn) {
        this.dn = dn;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    public String getIs() {
        return is;
    }

    public void setIs(String is) {
        this.is = is;
    }

    public String getMa() {
        return ma;
    }

    public void setMa(String ma) {
        this.ma = ma;
    }

    public String getMp() {
        return mp;
    }

    public void setMp(String mp) {
        this.mp = mp;
    }

    public Integer getSd() {
        return sd;
    }

    public void setSd(Integer sd) {
        this.sd = sd;
    }

    public String getTg() {
        return tg;
    }

    public void setTg(String tg) {
        this.tg = tg;
    }

    public String getVp() {
        return vp;
    }

    public void setVp(String vp) {
        this.vp = vp;
    }
}
