package ch.admin.bag.covidcertificate.backend.transformation.model;

import ch.admin.bag.covidcertificate.sdk.core.models.healthcert.CertificateHolder;
import ch.admin.bag.covidcertificate.sdk.core.models.state.CheckSignatureState;
import ch.admin.bag.covidcertificate.sdk.core.models.state.VerificationState.ERROR;
import ch.admin.bag.covidcertificate.sdk.core.models.state.VerificationState.INVALID;
import ch.admin.bag.covidcertificate.sdk.core.models.state.VerificationState.SUCCESS;

public class VerificationResponse {

    private SUCCESS successState;
    private ERROR errorState;
    private INVALID invalidState;
    private CertificateHolder hcertDecoded;

    public SUCCESS getSuccessState() {
        return successState;
    }

    public void setSuccessState(SUCCESS successState) {
        this.successState = successState;
    }

    public CertificateHolder getHcertDecoded() {
        return hcertDecoded;
    }

    public void setHcertDecoded(CertificateHolder hcertDecoded) {
        this.hcertDecoded = hcertDecoded;
    }

    public ERROR getErrorState() {
        return errorState;
    }

    public void setErrorState(ERROR errorState) {
        this.errorState = errorState;
    }

    public INVALID getInvalidState() {
        return invalidState;
    }

    public void setInvalidState(INVALID invalidState) {
        this.invalidState = invalidState;
    }

    public boolean isValid() {
        return successState != null;
    }

    public boolean signatureIsValid() {
        return successState != null
                || (errorState == null
                        && (invalidState == null
                                || invalidState.getSignatureState() == null
                                || (invalidState.getSignatureState()
                                        instanceof CheckSignatureState.SUCCESS)));
    }
}
