
# java-digital-sign
`java-digital-sign` library is a wrapper for [DSS Library](https://github.com/esig/dss)  which allows you to simplify some of the common tasks to perform when signing a PDF file.

> ⚠️ Currently the library only allows you to work on PDF files with a
> PAdES structure.

# Requirements
The latest version of `java-digital-sign` has the following minimal requirements:

-   Java 11 and higher (tested up to Java 17) for the build is required.
-   Maven 3.6 and higher;
-   Memory and Disk: see minimal requirements for the used JVM. In general the higher available is better;
-   Operating system: no specific requirements (tested on Windows and Linux).

# Build and Usage
A simple build of the `java-digital-sign` Maven project can be done with the following command:
```
mvn clean install
```
the package is complete with plugins for prettier and syntax check

### Validate
```
mvn validate
```
## Compile

to compile the JAR
```
mvn clean package
```

## Usage
Add dependecy in pom.xml
```xml
<dependency>
    <groupId>it.pagopa.dss</groupId>
    <artifactId>java-digital-sign</artifactId>
    <version>0.0.1</version>
</dependency>
```

Generate a PAdES PDF with a signature placeholder
```java
File inputFile = new File("input.pdf");
File outputFile = new File("pades.pdf");
String fieldId = "SignatureFieldId";

FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
SignatureServiceInterface serviceInterface = SignatureService.getInterface();

serviceInterface.generatePadesFile(inputFile, fileOutputStream, fieldId);
```

Put a signatureValue in a PAdES PDF
```java
File inputFile = new File("pades.pdf");
File outputFile = new File("signed.pdf");
String fieldId = "SignatureFieldId";
byte[] signatureValue = [.....];

FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
SignatureServiceInterface serviceInterface = SignatureService.getInterface();

serviceInterface.addSignatureToPadesFile(inputFile, fileOutputStream, fieldId, signatureValue);
```
