package com.factuec.infrastructure.sri;

import com.factuec.domain.enums.EstadoSri;
import java.util.List;

public record SriReceptionResult(
        EstadoSri estado,
        List<SriResponseMessage> mensajes
) {
}
