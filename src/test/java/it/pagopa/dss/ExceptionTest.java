package it.pagopa.dss;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import it.pagopa.dss.exception.SignatureServiceException;
import org.junit.jupiter.api.Test;

public class ExceptionTest {

  @Test
  public void assertException() {
    SignatureServiceException ex = new SignatureServiceException("This is an exception");
    SignatureServiceException ex2 = new SignatureServiceException(
      "This is an exception",
      ex
    );
    assertNotNull(ex);
    assertNotNull(ex2);
  }
}
