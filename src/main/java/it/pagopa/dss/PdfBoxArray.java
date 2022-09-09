package it.pagopa.dss;

import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.pdf.PdfArray;
import eu.europa.esig.dss.pdf.PdfDict;
import eu.europa.esig.dss.spi.DSSUtils;
import java.io.IOException;
import java.io.InputStream;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSBoolean;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNull;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The PDFBox implementation of {@code eu.europa.esig.dss.pdf.PdfArray}.
 */
class PdfBoxArray implements PdfArray {

  private static final Logger LOG = LoggerFactory.getLogger(PdfBoxArray.class);

  /** The PDFBox object */
  private COSArray wrapped;

  /**
   * The document
   *
   * NOTE for developers: Retain this reference ! PDDocument must not be garbage collected
   */
  private PDDocument document;

  /**
   * Default constructor
   *
   * @param wrapped {@link COSArray}
   * @param document {@link PDDocument}
   */
  PdfBoxArray(COSArray wrapped, PDDocument document) {
    this.wrapped = wrapped;
    this.document = document;
  }

  @Override
  public int size() {
    return wrapped.size();
  }

  @Override
  public byte[] getStreamBytes(int i) throws IOException {
    COSBase val = wrapped.get(i);
    return toBytes(val);
  }

  private byte[] toBytes(COSBase val) throws IOException {
    COSStream cosStream = null;
    if (val instanceof COSObject) {
      COSObject o = (COSObject) val;
      final COSBase object = o.getObject();
      if (object instanceof COSStream) {
        cosStream = (COSStream) object;
      }
    }
    if (cosStream == null) {
      throw new DSSException(
        "Cannot find value for " + val + " of class " + val.getClass()
      );
    }
    try (InputStream is = cosStream.createInputStream()) {
      byte[] result = DSSUtils.toByteArray(is);
      cosStream.close();
      return result;
    }
  }

  @Override
  public Long getObjectNumber(int i) {
    COSBase val = wrapped.get(i);
    if (val instanceof COSObject) {
      return ((COSObject) val).getObjectNumber();
    }
    return null;
  }

  @Override
  public Number getNumber(int i) {
    COSBase val = wrapped.get(i);
    if (val != null) {
      if (val instanceof COSFloat) {
        return ((COSFloat) val).floatValue();
      } else if (val instanceof COSNumber) {
        return ((COSNumber) val).longValue();
      }
    }
    return null;
  }

  @Override
  public String getString(int i) {
    return wrapped.getString(i);
  }

  @Override
  public PdfDict getAsDict(int i) {
    COSDictionary cosDictionary = null;
    COSBase cosBaseObject = wrapped.get(i);
    if (cosBaseObject instanceof COSDictionary) {
      cosDictionary = (COSDictionary) cosBaseObject;
    } else if (cosBaseObject instanceof COSObject) {
      COSObject cosObject = (COSObject) cosBaseObject;
      cosDictionary = (COSDictionary) cosObject.getObject();
    }
    if (cosDictionary != null) {
      return new PdfBoxDict(cosDictionary, document);
    }
    LOG.warn("Unable to extract array entry as dictionary!");
    return null;
  }

  @Override
  public Object getObject(int i) {
    COSBase dictionaryObject = wrapped.getObject(i);
    if (dictionaryObject == null) {
      return null;
    }
    if (
      dictionaryObject instanceof COSDictionary || dictionaryObject instanceof COSObject
    ) {
      return getAsDict(i);
    } else if (dictionaryObject instanceof COSArray) {
      return new PdfBoxArray((COSArray) dictionaryObject, document);
    } else if (dictionaryObject instanceof COSString) {
      return getString(i);
    } else if (dictionaryObject instanceof COSName) {
      return wrapped.getName(i);
    } else if (dictionaryObject instanceof COSNumber) {
      return getNumber(i);
    } else if (dictionaryObject instanceof COSBoolean) {
      return ((COSBoolean) dictionaryObject).getValueAsObject();
    } else if (dictionaryObject instanceof COSNull) {
      return null;
    } else {
      LOG.warn(
        "Unable to process an entry on position '{}' of type '{}'.",
        i,
        dictionaryObject.getClass()
      );
    }
    return null;
  }

  @Override
  public String toString() {
    return wrapped.toString();
  }
}
