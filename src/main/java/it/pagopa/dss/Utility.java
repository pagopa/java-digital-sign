package it.pagopa.dss;

import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.pades.exception.InvalidPasswordException;
import eu.europa.esig.dss.pades.validation.ByteRange;
import eu.europa.esig.dss.pdf.PAdESConstants;
import eu.europa.esig.dss.pdf.PdfArray;
import eu.europa.esig.dss.pdf.PdfDict;
import eu.europa.esig.dss.pdf.pdfbox.PdfBoxDocumentReader;
import eu.europa.esig.dss.utils.Utils;
import it.pagopa.dss.exception.SignatureServiceException;
import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class Utility {

  private Utility() {}

  private static final Logger LOG = LoggerFactory.getLogger(Utility.class);

  static ByteRange getByteRange(DSSDocument documentToSign, String signatureFieldId)
    throws IOException, SignatureServiceException {
    try (PdfBoxDocumentReader reader = new PdfBoxDocumentReader(documentToSign)) {
      PDDocument pdDocument = reader.getPDDocument();
      List<PDSignatureField> pdSignatureFields = pdDocument.getSignatureFields();

      if (Utils.isCollectionNotEmpty(pdSignatureFields)) {
        for (PDSignatureField signatureField : pdSignatureFields) {
          if (signatureField.getFullyQualifiedName().equalsIgnoreCase(signatureFieldId)) {
            COSObject sigDictObject = signatureField
              .getCOSObject()
              .getCOSObject(COSName.V);
            if (
              sigDictObject == null ||
              !(sigDictObject.getObject() instanceof COSDictionary)
            ) {
              LOG.warn("Skip field '%s'", signatureField.getFullyQualifiedName());
              continue;
            }

            PdfDict dictionary = new PdfBoxDict(
              (COSDictionary) sigDictObject.getObject(),
              pdDocument
            );
            PdfArray byteRangeArray = dictionary.getAsArray(
              PAdESConstants.BYTE_RANGE_NAME
            );

            if (byteRangeArray == null) {
              LOG.error(
                "Unable to retrieve the '%s' field value.",
                PAdESConstants.BYTE_RANGE_NAME
              );
              throw new SignatureServiceException(
                String.format(
                  "Unable to retrieve the '%s' field value.",
                  PAdESConstants.BYTE_RANGE_NAME
                )
              );
            }

            int arraySize = byteRangeArray.size();
            int[] result = new int[arraySize];
            for (int i = 0; i < arraySize; i++) {
              result[i] = byteRangeArray.getNumber(i).intValue();
            }

            ByteRange range = new ByteRange(result);
            return range;
          }
        }
        LOG.error("'%s' field not found!", signatureFieldId);
        throw new SignatureServiceException(
          String.format("'%s' field not found!", signatureFieldId)
        );
      } else {
        LOG.error("No signature fields found");
        throw new SignatureServiceException(String.format("No signature fields found"));
      }
    } catch (InvalidPasswordException e) {
      LOG.error("This file is protected by password!");
      throw new SignatureServiceException(
        String.format("This file is protected by password!")
      );
    }
  }
}
