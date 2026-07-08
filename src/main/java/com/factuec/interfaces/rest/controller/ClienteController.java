package com.factuec.interfaces.rest.controller;

import com.factuec.application.dto.cliente.ClienteRequest;
import com.factuec.application.dto.cliente.ClienteResponse;
import com.factuec.application.usecase.ClienteUseCase;
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
@RequestMapping("/api/clientes")
@Tag(name = "Clientes")
public class ClienteController {
    private final ClienteUseCase clienteUseCase;

    public ClienteController(ClienteUseCase clienteUseCase) {
        this.clienteUseCase = clienteUseCase;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMISOR','CONTABILIDAD','CONSULTA','SOPORTE')")
    ApiResponse<List<ClienteResponse>> list(@RequestParam(required = false) UUID empresaId) {
        return ApiResponse.ok(clienteUseCase.list(empresaId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMISOR','CONTABILIDAD')")
    ApiResponse<ClienteResponse> create(@Valid @RequestBody ClienteRequest request) {
        return ApiResponse.ok("Cliente creado", clienteUseCase.create(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMISOR','CONTABILIDAD','CONSULTA','SOPORTE')")
    ApiResponse<ClienteResponse> get(@PathVariable UUID id) {
        return ApiResponse.ok(clienteUseCase.get(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMISOR','CONTABILIDAD')")
    ApiResponse<ClienteResponse> update(@PathVariable UUID id, @Valid @RequestBody ClienteRequest request) {
        return ApiResponse.ok("Cliente actualizado", clienteUseCase.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMISOR','CONTABILIDAD')")
    ApiResponse<Void> delete(@PathVariable UUID id) {
        clienteUseCase.delete(id);
        return ApiResponse.ok("Cliente inactivado", null);
    }
}
