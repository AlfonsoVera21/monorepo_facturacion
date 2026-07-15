package com.factuec.application.dto.comprobante;

import com.factuec.domain.enums.TipoIdentificacion;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record GuiaRemisionRequest(
        @NotNull UUID empresaId,
        @NotNull UUID clienteId,
        @NotNull UUID establecimientoId,
        @NotNull UUID puntoEmisionId,
        LocalDate fechaEmision,
        @NotBlank @Size(max = 300) String dirPartida,
        @NotBlank @Size(max = 300) String razonSocialTransportista,
        @NotNull TipoIdentificacion tipoIdentificacionTransportista,
        @NotBlank @Size(max = 20) String identificacionTransportista,
        @Size(max = 40) String rise,
        @NotNull LocalDate fechaIniTransporte,
        @NotNull LocalDate fechaFinTransporte,
        @NotBlank @Size(max = 20) String placa,
        @Size(max = 300) String destinatarioDireccion,
        @NotBlank @Size(max = 300) String motivoTraslado,
        @Size(max = 20) String docAduaneroUnico,
        @Size(max = 3) String codEstabDestino,
        @Size(max = 300) String ruta,
        @Size(max = 2) String codDocSustento,
        @Size(max = 17) String numDocSustento,
        @Size(max = 49) String numAutDocSustento,
        LocalDate fechaEmisionDocSustento,
        @Valid @NotEmpty List<GuiaRemisionDetalleRequest> detalles
) {
    @AssertTrue(message = "La fecha de inicio de transporte no puede ser menor a la fecha de emision")
    public boolean isFechaInicioTransporteValida() {
        LocalDate emision = fechaEmision == null ? LocalDate.now() : fechaEmision;
        return fechaIniTransporte == null || !fechaIniTransporte.isBefore(emision);
    }

    @AssertTrue(message = "La fecha fin de transporte no puede ser menor a la fecha de inicio")
    public boolean isFechaFinTransporteValida() {
        return fechaIniTransporte == null || fechaFinTransporte == null || !fechaFinTransporte.isBefore(fechaIniTransporte);
    }
}
