package com.factuec.interfaces.rest.controller;

import com.factuec.application.dto.empresa.EstablecimientoRequest;
import com.factuec.application.dto.empresa.EstablecimientoResponse;
import com.factuec.application.dto.empresa.PuntoEmisionRequest;
import com.factuec.application.dto.empresa.PuntoEmisionResponse;
import com.factuec.application.dto.empresa.SecuencialRequest;
import com.factuec.application.dto.empresa.SecuencialResponse;
import com.factuec.application.usecase.EmisionConfigUseCase;
import com.factuec.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Configuracion emision")
public class EmisionConfigController {
    private final EmisionConfigUseCase useCase;

    public EmisionConfigController(EmisionConfigUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/establecimientos")
    @PreAuthorize("hasAnyRole('ADMIN','EMISOR','CONTABILIDAD','CONSULTA','SOPORTE')")
    ApiResponse<List<EstablecimientoResponse>> establecimientos(@RequestParam UUID empresaId) {
        return ApiResponse.ok(useCase.listEstablecimientos(empresaId));
    }

    @PostMapping("/establecimientos")
    @PreAuthorize("hasAnyRole('ADMIN','EMISOR','CONTABILIDAD')")
    ApiResponse<EstablecimientoResponse> createEstablecimiento(@Valid @RequestBody EstablecimientoRequest request) {
        return ApiResponse.ok("Establecimiento creado", useCase.createEstablecimiento(request));
    }

    @PutMapping("/establecimientos/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMISOR','CONTABILIDAD')")
    ApiResponse<EstablecimientoResponse> updateEstablecimiento(@PathVariable UUID id, @Valid @RequestBody EstablecimientoRequest request) {
        return ApiResponse.ok("Establecimiento actualizado", useCase.updateEstablecimiento(id, request));
    }

    @GetMapping("/puntos-emision")
    @PreAuthorize("hasAnyRole('ADMIN','EMISOR','CONTABILIDAD','CONSULTA','SOPORTE')")
    ApiResponse<List<PuntoEmisionResponse>> puntos(@RequestParam UUID establecimientoId) {
        return ApiResponse.ok(useCase.listPuntos(establecimientoId));
    }

    @PostMapping("/puntos-emision")
    @PreAuthorize("hasAnyRole('ADMIN','EMISOR','CONTABILIDAD')")
    ApiResponse<PuntoEmisionResponse> createPunto(@Valid @RequestBody PuntoEmisionRequest request) {
        return ApiResponse.ok("Punto de emision creado", useCase.createPunto(request));
    }

    @PutMapping("/puntos-emision/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMISOR','CONTABILIDAD')")
    ApiResponse<PuntoEmisionResponse> updatePunto(@PathVariable UUID id, @Valid @RequestBody PuntoEmisionRequest request) {
        return ApiResponse.ok("Punto de emision actualizado", useCase.updatePunto(id, request));
    }

    @PostMapping("/secuenciales")
    @PreAuthorize("hasAnyRole('ADMIN','EMISOR','CONTABILIDAD')")
    ApiResponse<SecuencialResponse> upsertSecuencial(@Valid @RequestBody SecuencialRequest request) {
        return ApiResponse.ok("Secuencial configurado", useCase.upsertSecuencial(request));
    }
}
