package ch.admin.bag.covidcertificate.backend.transformation.model.pdf;

/**
 * This is a java implementation of the `test_entry` json spec found at
 * https://github.com/ehn-dcc-development/ehn-dcc-schema/blob/1.0.0/DGC.combined-schema.json
 */
public class TestCert {
    private String ci;
    private String co;
    private String is;
    private String ma;
    private String nm;
    private String sc;
    private String tc;
    private String tg;
    private String tr;
    private String tt;

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

    public String getNm() {
        return nm;
    }

    public void setNm(String nm) {
        this.nm = nm;
    }

    public String getSc() {
        return sc;
    }

    public void setSc(String sc) {
        this.sc = sc;
    }

    public String getTc() {
        return tc;
    }

    public void setTc(String tc) {
        this.tc = tc;
    }

    public String getTg() {
        return tg;
    }

    public void setTg(String tg) {
        this.tg = tg;
    }

    public String getTr() {
        return tr;
    }

    public void setTr(String tr) {
        this.tr = tr;
    }

    public String getTt() {
        return tt;
    }

    public void setTt(String tt) {
        this.tt = tt;
    }
}
