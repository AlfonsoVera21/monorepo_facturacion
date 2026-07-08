package com.factuec.infrastructure.sri;

import com.factuec.domain.enums.EstadoSri;
import java.time.Instant;
import java.util.List;

public record SriAuthorizationResult(
        EstadoSri estado,
        String numeroAutorizacion,
        Instant fechaAutorizacion,
        String xmlAutorizado,
        List<SriResponseMessage> mensajes
) {
}
