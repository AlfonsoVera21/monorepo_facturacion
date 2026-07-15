package com.factuec.application.dto.chofer;

import com.factuec.domain.enums.TipoIdentificacion;
import com.factuec.domain.enums.UnidadMedidaInventario;
import java.math.BigDecimal;
import java.util.UUID;

public record ChoferResponse(
        UUID id,
        UUID empresaId,
        TipoIdentificacion tipoIdentificacion,
        String identificacion,
        String nombres,
        String apellidos,
        String licencia,
        String telefono,
        String correo,
        String placaVehiculo,
        String tipoVehiculo,
        BigDecimal capacidad,
        UnidadMedidaInventario unidadCapacidad,
        boolean transportaRefrigerado,
        boolean activo
) {
}
