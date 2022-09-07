package it.pagopa.dss;

import it.pagopa.dss.exception.SignatureServiceException;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public interface SignatureServiceInterface {
  /**
   * Add a PAdES structure to an input PDF and save it in an outputStream
   * @param originalPdf pdf file to which to add the PAdES structure
   * @param outputStream outputstream on which to write the PDF file with the added PAdES structure
   * @throws IOException
   * @throws SignatureServiceException
   */
  void generatePadesFile(File originalPdf, OutputStream outputStream)
    throws IOException, SignatureServiceException;

  /**
   * Add a PAdES structure to an input PDF and save it in an outputStream
   * @param originalPdf pdf file to which to add the PAdES structure
   * @param outputStream outputstream on which to write the PDF file with the added PAdES structure
   * @param signatureFieldId id of the signature field present on the PDF file
   * @throws IOException
   * @throws SignatureServiceException
   */
  void generatePadesFile(
    File originalPdf,
    OutputStream outputStream,
    String signatureFieldId
  ) throws IOException, SignatureServiceException;

  /**
   * Add a PAdES structure to an input PDF and save it in an outputStream
   * @param originalPdf pdf file to which to add the PAdES structure
   * @param outputStream outputstream on which to write the PDF file with the added PAdES structure
   * @param signatureFieldId id of the signature field present on the PDF file
   * @param signatureText text to add to the signature field
   * @throws IOException
   * @throws SignatureServiceException
   */
  void generatePadesFile(
    File originalPdf,
    OutputStream outputStream,
    String signatureFieldId,
    String signatureText
  ) throws IOException, SignatureServiceException;

  /**
   * Adds a signed hash to a PAdES format file and saves it in an outputStream given in input
   * @param padesPdf PDF file with related PAdES structure
   * @param outputStream outputstream on which to write the PDF signed
   * @param signatureFieldId id of the signature field present on the PDF file
   * @param signatureValue byte array of the signature
   * @param signatureHexEncoded true if signatureValue is already Hex encoded
   * @throws IOException
   * @throws SignatureServiceException
   */
  void addSignatureToPadesFile(
    File padesPdf,
    OutputStream outputStream,
    String signatureFieldId,
    byte[] signatureValue,
    boolean signatureHexEncoded
  ) throws IOException, SignatureServiceException;
}
