package com.factuec.infrastructure.sri;

import com.factuec.application.port.out.SriReceptionPort;
import com.factuec.config.FactuEcProperties;
import com.factuec.domain.enums.AmbienteSri;
import com.factuec.domain.enums.EstadoSri;
import com.factuec.shared.exception.BusinessException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.client.RestClient;

@Service
public class SriSoapReceptionAdapter implements SriReceptionPort {
    private static final Pattern ESTADO_PATTERN = Pattern.compile("<estado>(.*?)</estado>", Pattern.DOTALL);
    private static final Pattern MESSAGE_PATTERN = Pattern.compile(
            "<mensaje>\\s*(?:<identificador>(.*?)</identificador>)?\\s*<mensaje>(.*?)</mensaje>\\s*(?:<informacionAdicional>(.*?)</informacionAdicional>)?\\s*(?:<tipo>(.*?)</tipo>)?\\s*</mensaje>",
            Pattern.DOTALL);

    private final FactuEcProperties properties;
    private final RestClient restClient;

    public SriSoapReceptionAdapter(FactuEcProperties properties) {
        this.properties = properties;
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(properties.soap().connectTimeout());
        requestFactory.setReadTimeout(properties.soap().readTimeout());
        this.restClient = RestClient.builder().requestFactory(requestFactory).build();
    }

    @Override
    public SriReceptionResult enviarComprobante(AmbienteSri ambiente, String xmlFirmado) {
        if (properties.sri().mockEnabled()) {
            return new SriReceptionResult(EstadoSri.RECIBIDA,
                    List.of(new SriResponseMessage("MOCK", "Comprobante recibido en modo mock", null, "INFO")));
        }
        try {
            String body = receptionEnvelope(xmlFirmado);
            String response = restClient.post()
                    .uri(receptionUrl(ambiente))
                    .contentType(MediaType.TEXT_XML)
                    .body(body)
                    .retrieve()
                    .body(String.class);
            EstadoSri estado = extractEstado(response);
            return new SriReceptionResult(estado, parseMessages(response, "Respuesta recepcion SRI", estado));
        } catch (Exception exception) {
            throw new BusinessException("Error al enviar comprobante al SRI: " + exception.getMessage());
        }
    }

    private EstadoSri extractEstado(String response) {
        if (response == null) {
            return EstadoSri.ERROR;
        }
        var matcher = ESTADO_PATTERN.matcher(response);
        if (!matcher.find()) {
            return response.contains("DEVUELTA") ? EstadoSri.DEVUELTA : EstadoSri.RECIBIDA;
        }
        return "DEVUELTA".equalsIgnoreCase(clean(matcher.group(1))) ? EstadoSri.DEVUELTA : EstadoSri.RECIBIDA;
    }

    private List<SriResponseMessage> parseMessages(String response, String fallback, EstadoSri estado) {
        if (response == null || response.isBlank()) {
            return List.of(new SriResponseMessage(null, fallback, response, estado.name()));
        }
        var messages = new ArrayList<SriResponseMessage>();
        var matcher = MESSAGE_PATTERN.matcher(response);
        while (matcher.find()) {
            messages.add(new SriResponseMessage(
                    clean(matcher.group(1)),
                    clean(matcher.group(2)),
                    clean(matcher.group(3)),
                    clean(matcher.group(4))));
        }
        return messages.isEmpty()
                ? List.of(new SriResponseMessage(null, fallback, response, estado.name()))
                : messages;
    }

    private String clean(String value) {
        if (value == null) {
            return null;
        }
        return HtmlUtils.htmlUnescape(value.strip());
    }

    private String receptionEnvelope(String xmlFirmado) {
        String base64 = Base64.getEncoder().encodeToString(xmlFirmado.getBytes(StandardCharsets.UTF_8));
        return """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ec="http://ec.gob.sri.ws.recepcion">
                   <soapenv:Header/>
                   <soapenv:Body>
                      <ec:validarComprobante>
                         <xml>%s</xml>
                      </ec:validarComprobante>
                   </soapenv:Body>
                </soapenv:Envelope>
                """.formatted(base64);
    }

    private String receptionUrl(AmbienteSri ambiente) {
        String url = ambiente == AmbienteSri.PRODUCCION
                ? properties.sri().recepcionProduccionUrl()
                : properties.sri().recepcionPruebasUrl();
        return stripWsdl(url);
    }

    private String stripWsdl(String url) {
        return url == null ? null : url.replace("?wsdl", "").replace("?WSDL", "");
    }
}
