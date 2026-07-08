package com.factuec.infrastructure.sri;

import com.factuec.application.port.out.SriAuthorizationPort;
import com.factuec.config.FactuEcProperties;
import com.factuec.domain.enums.AmbienteSri;
import com.factuec.domain.enums.EstadoSri;
import com.factuec.shared.exception.BusinessException;
import java.time.Instant;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class SriSoapAuthorizationAdapter implements SriAuthorizationPort {
    private static final Pattern AUTH_NUMBER = Pattern.compile("<numeroAutorizacion>(.*?)</numeroAutorizacion>");
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
            EstadoSri estado = response != null && response.contains("AUTORIZADO") ? EstadoSri.AUTORIZADO : EstadoSri.NO_AUTORIZADO;
            String numeroAutorizacion = extractAuthorizationNumber(response, claveAcceso);
            return new SriAuthorizationResult(
                    estado,
                    numeroAutorizacion,
                    estado == EstadoSri.AUTORIZADO ? Instant.now() : null,
                    null,
                    List.of(new SriResponseMessage(null, "Respuesta autorizacion SRI", response, estado.name())));
        } catch (Exception exception) {
            throw new BusinessException("Error al consultar autorizacion SRI: " + exception.getMessage());
        }
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

    private String extractAuthorizationNumber(String response, String fallback) {
        if (response == null) {
            return fallback;
        }
        var matcher = AUTH_NUMBER.matcher(response);
        return matcher.find() ? matcher.group(1) : fallback;
    }
}
