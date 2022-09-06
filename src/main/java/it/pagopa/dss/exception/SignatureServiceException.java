package it.pagopa.dss.exception;

public class SignatureServiceException extends Exception {

  public SignatureServiceException(String errorMessage) {
    super(errorMessage);
  }

  public SignatureServiceException(String errorMessage, Throwable err) {
    super(errorMessage, err);
  }
}
