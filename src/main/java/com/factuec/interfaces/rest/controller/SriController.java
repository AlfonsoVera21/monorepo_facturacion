package com.factuec.interfaces.rest.controller;

import com.factuec.application.dto.comprobante.ComprobanteResponse;
import com.factuec.application.dto.reporte.SriEstadoResponse;
import com.factuec.application.usecase.ComprobanteUseCase;
import com.factuec.config.FactuEcProperties;
import com.factuec.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sri")
@Tag(name = "SRI")
public class SriController {
    private final FactuEcProperties properties;
    private final ComprobanteUseCase comprobanteUseCase;

    public SriController(FactuEcProperties properties, ComprobanteUseCase comprobanteUseCase) {
        this.properties = properties;
        this.comprobanteUseCase = comprobanteUseCase;
    }

    @GetMapping("/estado")
    @PreAuthorize("hasAnyRole('ADMIN','SOPORTE','CONTABILIDAD')")
    ApiResponse<SriEstadoResponse> estado() {
        return ApiResponse.ok(new SriEstadoResponse(
                properties.sri().ambienteDefault(),
                properties.sri().mockEnabled(),
                properties.sri().recepcionPruebasUrl(),
                properties.sri().autorizacionPruebasUrl(),
                properties.sri().recepcionProduccionUrl(),
                properties.sri().autorizacionProduccionUrl()));
    }

    @GetMapping("/errores")
    @PreAuthorize("hasAnyRole('ADMIN','SOPORTE','CONTABILIDAD')")
    ApiResponse<List<String>> errores() {
        return ApiResponse.ok(List.of(
                "DEVUELTA: XML invalido, firma invalida o datos tributarios inconsistentes",
                "NO_AUTORIZADO: comprobante recibido pero no autorizado por validacion SRI",
                "ERROR: timeout, servicio no disponible o fallo de comunicacion SOAP"));
    }

    @PostMapping("/reenviar-pendientes")
    @PreAuthorize("hasAnyRole('ADMIN','SOPORTE','CONTABILIDAD')")
    ApiResponse<List<ComprobanteResponse>> reenviarPendientes() {
        return ApiResponse.ok("Pendientes reenviados", comprobanteUseCase.reenviarPendientes());
    }
}
