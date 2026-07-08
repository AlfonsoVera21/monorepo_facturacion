package com.factuec.interfaces.rest.controller;

import com.factuec.application.dto.empresa.EmpresaRequest;
import com.factuec.application.dto.empresa.EmpresaResponse;
import com.factuec.application.usecase.EmpresaUseCase;
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
@RequestMapping("/api/empresas")
@Tag(name = "Empresas")
public class EmpresaController {
    private final EmpresaUseCase empresaUseCase;

    public EmpresaController(EmpresaUseCase empresaUseCase) {
        this.empresaUseCase = empresaUseCase;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SOPORTE','CONTABILIDAD','CONSULTA')")
    ApiResponse<List<EmpresaResponse>> list() {
        return ApiResponse.ok(empresaUseCase.list());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SOPORTE')")
    ApiResponse<EmpresaResponse> create(@Valid @RequestBody EmpresaRequest request) {
        return ApiResponse.ok("Empresa creada", empresaUseCase.create(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SOPORTE','CONTABILIDAD','CONSULTA')")
    ApiResponse<EmpresaResponse> get(@PathVariable UUID id) {
        return ApiResponse.ok(empresaUseCase.get(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SOPORTE')")
    ApiResponse<EmpresaResponse> update(@PathVariable UUID id, @Valid @RequestBody EmpresaRequest request) {
        return ApiResponse.ok("Empresa actualizada", empresaUseCase.update(id, request));
    }
}
