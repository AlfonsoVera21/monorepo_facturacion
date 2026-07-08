package com.factuec.infrastructure.email;

import com.factuec.application.port.out.EmailPort;
import com.factuec.config.FactuEcProperties;
import com.factuec.infrastructure.persistence.entity.ComprobanteEntity;
import com.factuec.shared.exception.BusinessException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class SmtpEmailAdapter implements EmailPort {
    private final JavaMailSender mailSender;
    private final FactuEcProperties properties;

    public SmtpEmailAdapter(JavaMailSender mailSender, FactuEcProperties properties) {
        this.mailSender = mailSender;
        this.properties = properties;
    }

    @Override
    public void sendComprobante(ComprobanteEntity comprobante, byte[] ridePdf) {
        if (!properties.mail().enabled()) {
            return;
        }
        if (comprobante.getCliente().getCorreo() == null || comprobante.getCliente().getCorreo().isBlank()) {
            throw new BusinessException("El cliente no tiene correo configurado");
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(properties.mail().from());
            helper.setTo(comprobante.getCliente().getCorreo());
            helper.setSubject("Comprobante electronico " + comprobante.getNumeroCompleto());
            helper.setText("""
                    Estimado cliente,

                    Adjuntamos su comprobante electronico autorizado y su RIDE.

                    Saludos,
                    FactuEc
                    """);
            helper.addAttachment(comprobante.getNumeroCompleto() + ".xml",
                    new ByteArrayResource(comprobante.getXmlFirmado().getBytes(StandardCharsets.UTF_8)));
            helper.addAttachment(comprobante.getNumeroCompleto() + ".pdf", new ByteArrayResource(ridePdf));
            mailSender.send(message);
        } catch (Exception exception) {
            throw new BusinessException("No se pudo enviar correo: " + exception.getMessage());
        }
    }
}
