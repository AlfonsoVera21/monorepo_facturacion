package com.factuec.infrastructure.sri;

import com.factuec.application.port.out.SriReceptionPort;
import com.factuec.config.FactuEcProperties;
import com.factuec.domain.enums.AmbienteSri;
import com.factuec.domain.enums.EstadoSri;
import com.factuec.shared.exception.BusinessException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class SriSoapReceptionAdapter implements SriReceptionPort {
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
            EstadoSri estado = response != null && response.contains("DEVUELTA") ? EstadoSri.DEVUELTA : EstadoSri.RECIBIDA;
            return new SriReceptionResult(estado, List.of(new SriResponseMessage(null, "Respuesta recepcion SRI", response, estado.name())));
        } catch (Exception exception) {
            throw new BusinessException("Error al enviar comprobante al SRI: " + exception.getMessage());
        }
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
