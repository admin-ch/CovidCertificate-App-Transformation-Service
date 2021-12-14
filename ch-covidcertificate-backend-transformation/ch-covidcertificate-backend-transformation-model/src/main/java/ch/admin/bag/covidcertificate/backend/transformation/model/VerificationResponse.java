package ch.admin.bag.covidcertificate.backend.transformation.model;

import ch.admin.bag.covidcertificate.sdk.core.models.healthcert.CertificateHolder;
import ch.admin.bag.covidcertificate.sdk.core.models.state.CheckSignatureState;
import ch.admin.bag.covidcertificate.sdk.core.models.state.SuccessState.WalletSuccessState;
import ch.admin.bag.covidcertificate.sdk.core.models.state.VerificationState.ERROR;
import ch.admin.bag.covidcertificate.sdk.core.models.state.VerificationState.INVALID;

public class VerificationResponse {
    // This class replaces the SUCCESS class from the SDK since Jackson can't deal with abstract
    // classes well
    public static class WalletSuccessStateWrapper {
        // This field could be either this or VerifierSuccessState in the SUCCESS class, but we need
        // it to always be WalletSuccessState
        private WalletSuccessState successState;
        private boolean isLightCertificate;

        public WalletSuccessState getSuccessState() {
            return successState;
        }

        public void setSuccessState(WalletSuccessState successState) {
            this.successState = successState;
        }

        public boolean isLightCertificate() {
            return isLightCertificate;
        }

        public void setLightCertificate(boolean lightCertificate) {
            isLightCertificate = lightCertificate;
        }
    }

    private WalletSuccessStateWrapper successState;
    private ERROR errorState;
    private INVALID invalidState;
    private CertificateHolder hcertDecoded;

    public WalletSuccessStateWrapper getSuccessState() {
        return successState;
    }

    public void setSuccessState(WalletSuccessStateWrapper successState) {
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
