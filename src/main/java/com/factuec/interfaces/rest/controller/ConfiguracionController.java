package com.factuec.interfaces.rest.controller;

import com.factuec.application.dto.configuracion.ConfiguracionGeneralResponse;
import com.factuec.config.FactuEcProperties;
import com.factuec.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/configuracion")
@Tag(name = "Configuracion")
public class ConfiguracionController {
    private final FactuEcProperties properties;

    public ConfiguracionController(FactuEcProperties properties) {
        this.properties = properties;
    }

    @GetMapping("/general")
    @PreAuthorize("hasAnyRole('ADMIN','SOPORTE','CONTABILIDAD')")
    ApiResponse<ConfiguracionGeneralResponse> general() {
        return ApiResponse.ok(new ConfiguracionGeneralResponse(
                "Configurada por establecimiento",
                properties.sri().ambienteDefault(),
                properties.mail().from(),
                properties.mail().enabled(),
                Integer.toString(properties.soap().retries()),
                false,
                properties.sri().mockEnabled(),
                properties.signature().mockEnabled()));
    }
}
