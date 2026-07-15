package com.factuec.interfaces.rest.controller;

import com.factuec.application.dto.chofer.ChoferRequest;
import com.factuec.application.dto.chofer.ChoferResponse;
import com.factuec.application.usecase.ChoferUseCase;
import com.factuec.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/choferes")
@Tag(name = "Choferes")
public class ChoferController {
    private final ChoferUseCase choferUseCase;

    public ChoferController(ChoferUseCase choferUseCase) {
        this.choferUseCase = choferUseCase;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMISOR','CONTABILIDAD','CONSULTA','SOPORTE')")
    ApiResponse<List<ChoferResponse>> list(@RequestParam(required = false) UUID empresaId) {
        return ApiResponse.ok(choferUseCase.list(empresaId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMISOR','CONTABILIDAD')")
    ApiResponse<ChoferResponse> create(@Valid @RequestBody ChoferRequest request) {
        return ApiResponse.ok("Chofer creado", choferUseCase.create(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMISOR','CONTABILIDAD','CONSULTA','SOPORTE')")
    ApiResponse<ChoferResponse> get(@PathVariable UUID id) {
        return ApiResponse.ok(choferUseCase.get(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMISOR','CONTABILIDAD')")
    ApiResponse<ChoferResponse> update(@PathVariable UUID id, @Valid @RequestBody ChoferRequest request) {
        return ApiResponse.ok("Chofer actualizado", choferUseCase.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMISOR','CONTABILIDAD')")
    ApiResponse<Void> delete(@PathVariable UUID id) {
        choferUseCase.delete(id);
        return ApiResponse.ok("Chofer inactivado", null);
    }
}
