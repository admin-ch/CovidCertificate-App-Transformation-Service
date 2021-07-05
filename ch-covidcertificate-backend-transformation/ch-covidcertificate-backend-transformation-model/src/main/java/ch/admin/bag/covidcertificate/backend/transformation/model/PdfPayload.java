package ch.admin.bag.covidcertificate.backend.transformation.model;

import ch.ubique.openapi.docannotations.Documentation;
import javax.validation.constraints.NotNull;

public class PdfPayload {

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
