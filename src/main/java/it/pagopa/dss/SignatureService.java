package it.pagopa.dss;

public final class SignatureService {

  private SignatureService() {}

  /**
   * Create and get the interface to access the signature service
   * @return SignatureServiceInterface
   */
  public static SignatureServiceInterface getInterface() {
    return new SignatureServiceImpl();
  }
}
