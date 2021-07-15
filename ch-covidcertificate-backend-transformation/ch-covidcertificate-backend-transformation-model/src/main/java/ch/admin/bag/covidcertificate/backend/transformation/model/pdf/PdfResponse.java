package ch.admin.bag.covidcertificate.backend.transformation.model.pdf;

import ch.ubique.openapi.docannotations.Documentation;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PdfResponse {

    @Documentation(description = "Base-64-encoded covid certificate PDF")
    @NotNull
    private String pdf;

    public String getPdf() {
        return pdf;
    }

    public void setPdf(String pdf) {
        this.pdf = pdf;
    }
}
