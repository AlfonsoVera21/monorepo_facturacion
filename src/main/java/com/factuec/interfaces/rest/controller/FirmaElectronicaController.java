package com.factuec.interfaces.rest.controller;

import com.factuec.application.dto.firma.FirmaElectronicaRequest;
import com.factuec.application.dto.firma.FirmaElectronicaResponse;
import com.factuec.application.usecase.FirmaElectronicaUseCase;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/firmas")
@Tag(name = "Firmas electronicas")
public class FirmaElectronicaController {
    private final FirmaElectronicaUseCase firmaUseCase;

    public FirmaElectronicaController(FirmaElectronicaUseCase firmaUseCase) {
        this.firmaUseCase = firmaUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SOPORTE')")
    ApiResponse<FirmaElectronicaResponse> create(@Valid @RequestBody FirmaElectronicaRequest request) {
        return ApiResponse.ok("Firma registrada", firmaUseCase.create(request));
    }

    @GetMapping("/empresa/{empresaId}")
    @PreAuthorize("hasAnyRole('ADMIN','EMISOR','CONTABILIDAD','SOPORTE')")
    ApiResponse<List<FirmaElectronicaResponse>> findByEmpresa(@PathVariable UUID empresaId) {
        return ApiResponse.ok(firmaUseCase.findByEmpresa(empresaId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SOPORTE')")
    ApiResponse<FirmaElectronicaResponse> update(@PathVariable UUID id, @Valid @RequestBody FirmaElectronicaRequest request) {
        return ApiResponse.ok("Firma actualizada", firmaUseCase.update(id, request));
    }
}
