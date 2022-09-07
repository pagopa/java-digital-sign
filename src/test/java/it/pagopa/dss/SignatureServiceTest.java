package it.pagopa.dss;

import static org.assertj.core.api.Assertions.assertThat;

import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.pades.validation.ByteRange;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;

class SignatureServiceTest {

  private final SignatureServiceInterface serviceInterface = SignatureService.getInterface();
  private final String testFileName = "demo.pdf";
  private final String testFieldId = "Signature1";

  @Test
  public void assertByteRange() throws Exception {
    Path tempFile = Files.createTempFile(null, null);
    FileOutputStream fos = new FileOutputStream(tempFile.toFile());
    serviceInterface.generatePadesFile(new File(testFileName), fos, testFieldId);

    DSSDocument documentToSign = new FileDocument(tempFile.toFile());
    ByteRange range = Utility.getByteRange(documentToSign, testFieldId);

    assertThat(range.getFirstPartEnd()).isEqualTo(7622);
    assertThat(range.getSecondPartStart()).isEqualTo(26568);
  }

  @Test
  public void assertSignature() throws Exception {
    byte[] signatureValue = Hex.encodeHexString("INVALIDSIGNATURE".getBytes()).getBytes();

    Path tempPadesFile = Files.createTempFile(null, null);
    FileOutputStream fosPades = new FileOutputStream(tempPadesFile.toFile());
    serviceInterface.generatePadesFile(new File(testFileName), fosPades, testFieldId);

    Path tempSignedFile = Files.createTempFile(null, null);
    FileOutputStream fosSigned = new FileOutputStream(tempSignedFile.toFile());

    serviceInterface.addSignatureToPadesFile(
      tempPadesFile.toFile(),
      fosSigned,
      testFieldId,
      signatureValue,
      true
    );

    DSSDocument signedDocument = new FileDocument(tempSignedFile.toFile());
    ByteRange range = Utility.getByteRange(signedDocument, testFieldId);

    byte[] signedByte = Arrays.copyOfRange(
      Files.readAllBytes(tempSignedFile),
      range.getFirstPartEnd() + 1,
      range.getFirstPartEnd() + signatureValue.length + 1
    );

    assertThat(signedByte).isEqualTo(signatureValue);
  }
}
