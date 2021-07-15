package ch.admin.bag.covidcertificate.backend.transformation.model.pdf;

/**
 * This is a java implementation of the `recovery_entry` json spec found at
 * https://github.com/ehn-dcc-development/ehn-dcc-schema/blob/1.0.0/DGC.combined-schema.json
 */
public class RecoveryCert {
    private String ci;
    private String co;
    private String df;
    private String du;
    private String fr;
    private String is;
    private String tg;

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

    public String getDf() {
        return df;
    }

    public void setDf(String df) {
        this.df = df;
    }

    public String getDu() {
        return du;
    }

    public void setDu(String du) {
        this.du = du;
    }

    public String getFr() {
        return fr;
    }

    public void setFr(String fr) {
        this.fr = fr;
    }

    public String getIs() {
        return is;
    }

    public void setIs(String is) {
        this.is = is;
    }

    public String getTg() {
        return tg;
    }

    public void setTg(String tg) {
        this.tg = tg;
    }
}
