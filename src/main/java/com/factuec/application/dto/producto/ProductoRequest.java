package com.factuec.application.dto.producto;

import com.factuec.domain.enums.TarifaIva;
import com.factuec.domain.enums.TipoProducto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record ProductoRequest(
        @NotNull UUID empresaId,
        @NotBlank String codigoPrincipal,
        String codigoAuxiliar,
        @NotBlank String nombre,
        String descripcion,
        @NotNull TipoProducto tipo,
        @NotNull @DecimalMin("0.00") BigDecimal precioUnitario,
        @NotNull TarifaIva tarifaIva,
        BigDecimal icePorcentaje,
        BigDecimal stock,
        String categoria,
        Boolean activo
) {
}
