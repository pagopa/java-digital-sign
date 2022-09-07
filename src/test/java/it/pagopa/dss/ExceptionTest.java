package it.pagopa.dss;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import it.pagopa.dss.exception.SignatureServiceException;

public class ExceptionTest {

  @Test
  public void assertException(){
    SignatureServiceException ex = new SignatureServiceException("This is an exception");
    assertNotNull(ex);
  }
}
