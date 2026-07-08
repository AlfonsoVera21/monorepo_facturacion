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
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

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

            XMLSignatureFactory factory = XMLSignatureFactory.getInstance("DOM");
            Reference reference = factory.newReference(
                    "",
                    factory.newDigestMethod(DigestMethod.SHA256, null),
                    Collections.singletonList(factory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)),
                    null,
                    null);
            SignedInfo signedInfo = factory.newSignedInfo(
                    factory.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null),
                    factory.newSignatureMethod(SignatureMethod.RSA_SHA256, null),
                    Collections.singletonList(reference));

            KeyInfoFactory keyInfoFactory = factory.getKeyInfoFactory();
            X509Data x509Data = keyInfoFactory.newX509Data(Collections.singletonList(keyMaterial.certificate()));
            KeyInfo keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(x509Data));

            DOMSignContext signContext = new DOMSignContext(keyMaterial.privateKey(), document.getDocumentElement());
            XMLSignature signature = factory.newXMLSignature(signedInfo, keyInfo);
            signature.sign(signContext);
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
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        return writer.toString();
    }

    private record KeyMaterial(PrivateKey privateKey, X509Certificate certificate) {
    }
}
