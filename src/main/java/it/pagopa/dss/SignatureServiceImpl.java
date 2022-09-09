package it.pagopa.dss;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.pades.SignatureFieldParameters;
import eu.europa.esig.dss.pades.SignatureImageParameters;
import eu.europa.esig.dss.pades.SignatureImageTextParameters;
import eu.europa.esig.dss.pades.validation.ByteRange;
import eu.europa.esig.dss.pdf.IPdfObjFactory;
import eu.europa.esig.dss.pdf.PDFSignatureService;
import eu.europa.esig.dss.pdf.ServiceLoaderPdfObjFactory;
import eu.europa.esig.dss.pdf.pdfbox.visible.PdfBoxNativeFont;
import eu.europa.esig.dss.spi.DSSUtils;
import it.pagopa.dss.exception.SignatureServiceException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.codec.binary.Hex;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SignatureServiceImpl implements SignatureServiceInterface {

  private static final Logger LOG = LoggerFactory.getLogger(SignatureServiceImpl.class);
  private static final float DEFAULT_FONT_SIZE = 8;

  @Override
  public void generatePadesFile(File originalPdf, OutputStream outputStream)
    throws IOException {
    generatePadesFile(originalPdf, outputStream, null, null);
  }

  @Override
  public void generatePadesFile(
    File originalPdf,
    OutputStream outputStream,
    String signatureFieldId
  ) throws IOException {
    generatePadesFile(originalPdf, outputStream, signatureFieldId, null);
  }

  @Override
  public void generatePadesFile(
    File originalPdf,
    OutputStream outputStream,
    String signatureFieldId,
    String signatureText
  ) throws IOException {

    //converting the file into a DSS document (from dss library)
    DSSDocument documentToSign = new FileDocument(originalPdf);

    //initialization of PAdES parameters
    PAdESSignatureParameters parameters = new PAdESSignatureParameters();
    parameters.setDigestAlgorithm(DigestAlgorithm.SHA256);
    parameters.setGenerateTBSWithoutCertificate(true);

    IPdfObjFactory pdfObjFactory = new ServiceLoaderPdfObjFactory();
    PDFSignatureService pdfSignatureService = pdfObjFactory.newPAdESSignatureService();

    if (signatureFieldId != null) {
      //initialization of the parameters to set the signature field to be used
      SignatureImageParameters imageParameters = new SignatureImageParameters();
      SignatureFieldParameters fieldParameters = new SignatureFieldParameters();

      fieldParameters.setFieldId(signatureFieldId);
      imageParameters.setFieldParameters(fieldParameters);

      if (signatureText != null) {
        //set the text to be added in the signature field with its font
        SignatureImageTextParameters textParameters = new SignatureImageTextParameters();
        PdfBoxNativeFont font = new PdfBoxNativeFont(PDType1Font.HELVETICA);
        font.setSize(DEFAULT_FONT_SIZE);
        textParameters.setFont(font);
        textParameters.setText(signatureText);
        imageParameters.setTextParameters(textParameters);
      }

      parameters.setImageParameters(imageParameters);
    } else {
      LOG.warn("signatureFieldId is null, new field created!");
    }

    //the PAdES PDF that is generated initially has a signature consisting of all 0's which is used as a placeholder
    final byte[] emptySignatureValue = DSSUtils.EMPTY_BYTE_ARRAY;
    DSSDocument noSign = pdfSignatureService.sign(
      documentToSign,
      emptySignatureValue,
      parameters
    );
    noSign.writeTo(outputStream);
  }

  @Override
  public void addSignatureToPadesFile(
    File padesPdf,
    OutputStream outputStream,
    String signatureFieldId,
    byte[] signatureValue,
    boolean signatureHexEncoded
  ) throws IOException, SignatureServiceException {
    DSSDocument documentToSign = new FileDocument(padesPdf);
    ByteRange range = Utility.getByteRange(documentToSign, signatureFieldId);

    byte[] signatureHex = signatureValue;

    /*
     * The signature in order to be inserted inside a PAdES PDF must be HEX encoded.
     * ETSI EN 319 142-1
     */
    if (!signatureHexEncoded) {
      signatureHex = Hex.encodeHexString(signatureValue).getBytes();
    }

    BufferedInputStream inputBuffer = new BufferedInputStream(
      documentToSign.openStream()
    );
    BufferedOutputStream outBuffer = new BufferedOutputStream(outputStream);

    int ch;
    int i = 0;

    /*
     * The value of the signature must be inserted in place of the placeholder present in the PAdES PDF.
     * The starting byte of the placeholder is identified by range.getFirstPartEnd()
     */
    int offset = range.getFirstPartEnd();
    while ((ch = inputBuffer.read()) != -1) {
      if (i > offset && i <= offset + signatureHex.length) {
        ch = signatureHex[i - offset - 1];
      }
      outBuffer.write(ch);
      i += 1;
    }

    outBuffer.close();
    inputBuffer.close();
    outputStream.close();
  }
}
