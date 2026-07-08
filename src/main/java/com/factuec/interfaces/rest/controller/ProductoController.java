package com.factuec.interfaces.rest.controller;

import com.factuec.application.dto.producto.ProductoRequest;
import com.factuec.application.dto.producto.ProductoResponse;
import com.factuec.application.usecase.ProductoUseCase;
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
@RequestMapping("/api/productos")
@Tag(name = "Productos")
public class ProductoController {
    private final ProductoUseCase productoUseCase;

    public ProductoController(ProductoUseCase productoUseCase) {
        this.productoUseCase = productoUseCase;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMISOR','CONTABILIDAD','CONSULTA','SOPORTE')")
    ApiResponse<List<ProductoResponse>> list(@RequestParam(required = false) UUID empresaId) {
        return ApiResponse.ok(productoUseCase.list(empresaId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMISOR','CONTABILIDAD')")
    ApiResponse<ProductoResponse> create(@Valid @RequestBody ProductoRequest request) {
        return ApiResponse.ok("Producto creado", productoUseCase.create(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMISOR','CONTABILIDAD','CONSULTA','SOPORTE')")
    ApiResponse<ProductoResponse> get(@PathVariable UUID id) {
        return ApiResponse.ok(productoUseCase.get(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMISOR','CONTABILIDAD')")
    ApiResponse<ProductoResponse> update(@PathVariable UUID id, @Valid @RequestBody ProductoRequest request) {
        return ApiResponse.ok("Producto actualizado", productoUseCase.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMISOR','CONTABILIDAD')")
    ApiResponse<Void> delete(@PathVariable UUID id) {
        productoUseCase.delete(id);
        return ApiResponse.ok("Producto inactivado", null);
    }
}
