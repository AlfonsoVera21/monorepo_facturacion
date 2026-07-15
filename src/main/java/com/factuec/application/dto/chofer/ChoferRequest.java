package com.factuec.application.dto.chofer;

import com.factuec.domain.enums.TipoIdentificacion;
import com.factuec.domain.enums.UnidadMedidaInventario;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record ChoferRequest(
        @NotNull UUID empresaId,
        @NotNull TipoIdentificacion tipoIdentificacion,
        @NotBlank String identificacion,
        @NotBlank String nombres,
        String apellidos,
        @NotBlank String licencia,
        String telefono,
        @Email String correo,
        String placaVehiculo,
        String tipoVehiculo,
        @DecimalMin("0.00") BigDecimal capacidad,
        UnidadMedidaInventario unidadCapacidad,
        boolean transportaRefrigerado,
        Boolean activo
) {
}
