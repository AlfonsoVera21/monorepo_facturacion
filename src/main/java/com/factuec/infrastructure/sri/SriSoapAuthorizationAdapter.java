package com.factuec.infrastructure.sri;

import com.factuec.application.port.out.SriAuthorizationPort;
import com.factuec.config.FactuEcProperties;
import com.factuec.domain.enums.AmbienteSri;
import com.factuec.domain.enums.EstadoSri;
import com.factuec.shared.exception.BusinessException;
import java.util.ArrayList;
import java.time.Instant;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.client.RestClient;

@Service
public class SriSoapAuthorizationAdapter implements SriAuthorizationPort {
    private static final Pattern AUTH_NUMBER = Pattern.compile("<numeroAutorizacion>(.*?)</numeroAutorizacion>");
    private static final Pattern ESTADO_PATTERN = Pattern.compile("<estado>(.*?)</estado>", Pattern.DOTALL);
    private static final Pattern MESSAGE_PATTERN = Pattern.compile(
            "<mensaje>\\s*(?:<identificador>(.*?)</identificador>)?\\s*<mensaje>(.*?)</mensaje>\\s*(?:<informacionAdicional>(.*?)</informacionAdicional>)?\\s*(?:<tipo>(.*?)</tipo>)?\\s*</mensaje>",
            Pattern.DOTALL);

    private final FactuEcProperties properties;
    private final RestClient restClient;

    public SriSoapAuthorizationAdapter(FactuEcProperties properties) {
        this.properties = properties;
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(properties.soap().connectTimeout());
        requestFactory.setReadTimeout(properties.soap().readTimeout());
        this.restClient = RestClient.builder().requestFactory(requestFactory).build();
    }

    @Override
    public SriAuthorizationResult consultarAutorizacion(AmbienteSri ambiente, String claveAcceso) {
        if (properties.sri().mockEnabled()) {
            return new SriAuthorizationResult(
                    EstadoSri.AUTORIZADO,
                    claveAcceso,
                    Instant.now(),
                    null,
                    List.of(new SriResponseMessage("MOCK", "Comprobante autorizado en modo mock", null, "INFO")));
        }
        try {
            String response = restClient.post()
                    .uri(authorizationUrl(ambiente))
                    .contentType(MediaType.TEXT_XML)
                    .body(authorizationEnvelope(claveAcceso))
                    .retrieve()
                    .body(String.class);
            EstadoSri estado = extractEstado(response);
            String numeroAutorizacion = estado == EstadoSri.AUTORIZADO ? extractAuthorizationNumber(response) : null;
            return new SriAuthorizationResult(
                    estado,
                    numeroAutorizacion,
                    estado == EstadoSri.AUTORIZADO ? Instant.now() : null,
                    null,
                    parseMessages(response, "Respuesta autorizacion SRI", estado));
        } catch (Exception exception) {
            throw new BusinessException("Error al consultar autorizacion SRI: " + exception.getMessage());
        }
    }

    private EstadoSri extractEstado(String response) {
        if (response == null || response.isBlank()) {
            return EstadoSri.NO_AUTORIZADO;
        }
        var matcher = ESTADO_PATTERN.matcher(response);
        if (!matcher.find()) {
            return EstadoSri.NO_AUTORIZADO;
        }
        String value = clean(matcher.group(1)).replace(' ', '_');
        if ("AUTORIZADO".equalsIgnoreCase(value)) {
            return EstadoSri.AUTORIZADO;
        }
        return EstadoSri.NO_AUTORIZADO;
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
            return "";
        }
        return HtmlUtils.htmlUnescape(value.strip());
    }

    private String authorizationEnvelope(String claveAcceso) {
        return """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ec="http://ec.gob.sri.ws.autorizacion">
                   <soapenv:Header/>
                   <soapenv:Body>
                      <ec:autorizacionComprobante>
                         <claveAccesoComprobante>%s</claveAccesoComprobante>
                      </ec:autorizacionComprobante>
                   </soapenv:Body>
                </soapenv:Envelope>
                """.formatted(claveAcceso);
    }

    private String authorizationUrl(AmbienteSri ambiente) {
        String url = ambiente == AmbienteSri.PRODUCCION
                ? properties.sri().autorizacionProduccionUrl()
                : properties.sri().autorizacionPruebasUrl();
        return stripWsdl(url);
    }

    private String stripWsdl(String url) {
        return url == null ? null : url.replace("?wsdl", "").replace("?WSDL", "");
    }

    private String extractAuthorizationNumber(String response) {
        if (response == null) {
            return null;
        }
        var matcher = AUTH_NUMBER.matcher(response);
        return matcher.find() ? matcher.group(1) : null;
    }
}
