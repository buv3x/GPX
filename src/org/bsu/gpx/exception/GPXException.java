package org.bsu.gpx.exception;

public class GPXException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 5643302805011530184L;

    private ErrorCode errorCode;

    public GPXException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

}
