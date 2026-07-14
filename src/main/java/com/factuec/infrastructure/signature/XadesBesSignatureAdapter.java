package com.factuec.infrastructure.signature;

import com.factuec.application.port.out.SignaturePort;
import com.factuec.shared.exception.BusinessException;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertificateExpiredException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.apache.xml.security.signature.XMLSignature;
import xades4j.algorithms.EnvelopedSignatureTransform;
import xades4j.production.BasicSignatureOptions;
import xades4j.production.DataObjectReference;
import xades4j.production.SignatureAppendingStrategies;
import xades4j.production.SignatureAlgorithms;
import xades4j.production.SignedDataObjects;
import xades4j.production.SigningCertificateMode;
import xades4j.production.XadesBesSigningProfile;
import xades4j.production.XadesSigner;
import xades4j.properties.DataObjectDesc;
import xades4j.properties.DataObjectFormatProperty;
import xades4j.providers.impl.DirectKeyingDataProvider;

@Service
public class XadesBesSignatureAdapter implements SignaturePort {

    @Override
    public String firmarXml(String xml, FirmaConfig config) {
        if (xml == null || xml.isBlank()) {
            throw new BusinessException("XML invalido para firmar");
        }
        if (config.mockEnabled()) {
            return xml + "\n<!-- SIGNATURE_MOCK: habilitado solo por configuracion local/dev -->";
        }
        if (config.certificatePath() == null || config.password() == null || config.password().length == 0) {
            throw new BusinessException("Certificado o password de firma no configurado");
        }

        try {
            Document document = parseXml(xml);
            KeyMaterial keyMaterial = loadKeyMaterial(config);
            document.getDocumentElement().setIdAttribute("id", true);

            DirectKeyingDataProvider keyingDataProvider =
                    new DirectKeyingDataProvider(keyMaterial.certificate(), keyMaterial.privateKey());
            BasicSignatureOptions signatureOptions = new BasicSignatureOptions()
                    .includeSigningCertificate(SigningCertificateMode.SIGNING_CERTIFICATE)
                    .includeIssuerSerial(true)
                    .includeSubjectName(true)
                    .includePublicKey(true)
                    .signKeyInfo(true);
            SignatureAlgorithms signatureAlgorithms = new SignatureAlgorithms()
                    .withDigestAlgorithmForDataObjectReferences("http://www.w3.org/2000/09/xmldsig#sha1")
                    .withDigestAlgorithmForReferenceProperties("http://www.w3.org/2000/09/xmldsig#sha1")
                    .withDigestAlgorithmForTimeStampProperties("http://www.w3.org/2000/09/xmldsig#sha1")
                    .withSignatureAlgorithm("RSA", XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1);
            XadesSigner signer = new XadesBesSigningProfile(keyingDataProvider)
                    .withSignatureAlgorithms(signatureAlgorithms)
                    .withBasicSignatureOptions(signatureOptions)
                    .newSigner();

            DataObjectDesc dataObject = new DataObjectReference("#comprobante")
                    .withTransform(new EnvelopedSignatureTransform())
                    .withDataObjectFormat(new DataObjectFormatProperty("text/xml")
                            .withDescription("Comprobante electronico SRI"));
            signer.sign(new SignedDataObjects(dataObject), document.getDocumentElement(), SignatureAppendingStrategies.AsLastChild);
            return toString(document);
        } catch (CertificateExpiredException exception) {
            throw new BusinessException("Firma vencida");
        } catch (java.security.UnrecoverableKeyException exception) {
            throw new BusinessException("Password de certificado incorrecto");
        } catch (org.xml.sax.SAXException exception) {
            throw new BusinessException("XML invalido");
        } catch (Exception exception) {
            throw new BusinessException("Error al firmar XML: " + exception.getMessage());
        }
    }

    private Document parseXml(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    }

    private KeyMaterial loadKeyMaterial(FirmaConfig config) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (var inputStream = java.nio.file.Files.newInputStream(config.certificatePath())) {
            keyStore.load(inputStream, config.password());
        }

        String alias = Collections.list(keyStore.aliases()).stream()
                .filter(candidate -> {
                    try {
                        return keyStore.isKeyEntry(candidate);
                    } catch (Exception exception) {
                        return false;
                    }
                })
                .findFirst()
                .orElseThrow(() -> new BusinessException("Certificado invalido: no contiene llave privada"));

        PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, config.password());
        X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);
        certificate.checkValidity();
        return new KeyMaterial(privateKey, certificate);
    }

    private String toString(Document document) throws Exception {
        StringWriter writer = new StringWriter();
        var transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        return writer.toString();
    }

    private record KeyMaterial(PrivateKey privateKey, X509Certificate certificate) {
    }
}
