package ch.admin.bag.covidcertificate.backend.transformation.model.renewal;

public class CertRenewalException extends Exception {
    private static final long serialVersionUID = -7005211720159321539L;

    private Integer errorCode;
    private String httpStatus;

    public CertRenewalException() {}

    public CertRenewalException(BitCertRenewalErrorResponse errorResponse) {
        super(errorResponse.getErrorMessage());
        this.errorCode = errorResponse.getErrorCode();
        this.httpStatus = errorResponse.getHttpStatus();
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getHttpStatus() {
        return httpStatus;
    }
}
