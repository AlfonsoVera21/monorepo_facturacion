package com.factuec.application.dto.producto;

import com.factuec.domain.enums.TarifaIva;
import com.factuec.domain.enums.TipoProducto;
import java.math.BigDecimal;
import java.util.UUID;

public record ProductoResponse(
        UUID id,
        UUID empresaId,
        String codigoPrincipal,
        String codigoAuxiliar,
        String nombre,
        String descripcion,
        TipoProducto tipo,
        BigDecimal precioUnitario,
        TarifaIva tarifaIva,
        BigDecimal icePorcentaje,
        BigDecimal stock,
        String categoria,
        boolean activo
) {
}
