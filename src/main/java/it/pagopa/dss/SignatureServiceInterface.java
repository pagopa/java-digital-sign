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
   * @return void
   * @throws IOException
   */
  void generatePadesFile(File originalPdf, OutputStream outputStream) throws IOException;

  /**
   * Add a PAdES structure to an input PDF and save it in an outputStream
   * @param originalPdf pdf file to which to add the PAdES structure
   * @param outputStream outputstream on which to write the PDF file with the added PAdES structure
   * @param signatureFieldId id of the signature field present on the PDF file
   * @return void
   * @throws IOException
   */
  void generatePadesFile(
    File originalPdf,
    OutputStream outputStream,
    String signatureFieldId
  ) throws IOException;

  /**
   * Add a PAdES structure to an input PDF and save it in an outputStream
   * @param originalPdf pdf file to which to add the PAdES structure
   * @param outputStream outputstream on which to write the PDF file with the added PAdES structure
   * @param signatureFieldId id of the signature field present on the PDF file
   * @param signatureText text to add to the signature field
   * @return void
   * @throws IOException
   */
  void generatePadesFile(
    File originalPdf,
    OutputStream outputStream,
    String signatureFieldId,
    String signatureText
  ) throws IOException;

  /**
   * Adds a signed hash to a PAdES format file and saves it in an outputStream given in input
   * @param padesPdf PDF file with related PAdES structure
   * @param outputStream outputstream on which to write the PDF signed
   * @param signatureFieldId id of the signature field present on the PDF file
   * @param signatureValue byte array of the signature
   * @return void
   * @throws IOException
   */
  void addSignatureToPadesFile(
    File padesPdf,
    OutputStream outputStream,
    String signatureFieldId,
    byte[] signatureValue
  ) throws IOException, SignatureServiceException;
}