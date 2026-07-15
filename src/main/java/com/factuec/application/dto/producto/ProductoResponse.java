package com.factuec.application.dto.producto;

import com.factuec.domain.enums.TarifaIva;
import com.factuec.domain.enums.TipoProducto;
import com.factuec.domain.enums.UnidadMedidaInventario;
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
        UnidadMedidaInventario unidadMedida,
        BigDecimal stockMinimo,
        BigDecimal pesoPromedioKg,
        boolean palletizable,
        BigDecimal unidadesPorPallet,
        boolean requiereRefrigeracion,
        String categoria,
        boolean activo
) {
}
