package ch.admin.bag.covidcertificate.backend.transformation.ws.config.model;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "transform.pdf")
public class PdfConfig {
    private List<String> chIssuers;
    private String testEndpoint;
    private String recoveryEndpoint;
    private String vaccinationEndpoint;

    public List<String> getChIssuers() {
        return chIssuers;
    }

    public void setChIssuers(List<String> chIssuers) {
        this.chIssuers = chIssuers;
    }

    public String getTestEndpoint() {
        return testEndpoint;
    }

    public void setTestEndpoint(String testEndpoint) {
        this.testEndpoint = testEndpoint;
    }

    public String getRecoveryEndpoint() {
        return recoveryEndpoint;
    }

    public void setRecoveryEndpoint(String recoveryEndpoint) {
        this.recoveryEndpoint = recoveryEndpoint;
    }

    public String getVaccinationEndpoint() {
        return vaccinationEndpoint;
    }

    public void setVaccinationEndpoint(String vaccinationEndpoint) {
        this.vaccinationEndpoint = vaccinationEndpoint;
    }
}