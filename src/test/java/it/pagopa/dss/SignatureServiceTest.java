package it.pagopa.dss;

import static org.assertj.core.api.Assertions.assertThat;

import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.pades.validation.ByteRange;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class SignatureServiceTest {

  private final SignatureServiceInterface serviceInterface = SignatureService.getInterface();
  private final String testFileName = "demo.pdf";
  private final String testFieldId = "Signature1";

  @Test
  public void assertByteRange() {
    try {
      Path tempFile = Files.createTempFile(null, null);
      FileOutputStream fos = new FileOutputStream(tempFile.toFile());
      serviceInterface.generatePadesFile(new File(testFileName), fos, testFieldId);

      DSSDocument documentToSign = new FileDocument(tempFile.toFile());
      ByteRange range = Utility.getByteRange(documentToSign, testFieldId);

      assertThat(range.getFirstPartEnd()).isEqualTo(7622);
      assertThat(range.getSecondPartStart()).isEqualTo(26568);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
