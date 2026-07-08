package com.factuec.domain.service;

import com.factuec.domain.enums.EstadoComprobante;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class EstadoComprobantePolicy {
    private final Map<EstadoComprobante, EnumSet<EstadoComprobante>> allowedTransitions;

    public EstadoComprobantePolicy() {
        allowedTransitions = new EnumMap<>(EstadoComprobante.class);
        allowedTransitions.put(EstadoComprobante.BORRADOR, EnumSet.of(EstadoComprobante.GENERADO, EstadoComprobante.ANULADO));
        allowedTransitions.put(EstadoComprobante.GENERADO, EnumSet.of(EstadoComprobante.FIRMADO, EstadoComprobante.ERROR));
        allowedTransitions.put(EstadoComprobante.FIRMADO, EnumSet.of(EstadoComprobante.ENVIADO, EstadoComprobante.ERROR));
        allowedTransitions.put(EstadoComprobante.ENVIADO, EnumSet.of(EstadoComprobante.AUTORIZADO, EstadoComprobante.RECHAZADO, EstadoComprobante.DEVUELTO, EstadoComprobante.ERROR));
        allowedTransitions.put(EstadoComprobante.DEVUELTO, EnumSet.of(EstadoComprobante.FIRMADO, EstadoComprobante.ERROR));
        allowedTransitions.put(EstadoComprobante.RECHAZADO, EnumSet.of(EstadoComprobante.ANULADO));
        allowedTransitions.put(EstadoComprobante.ERROR, EnumSet.of(EstadoComprobante.GENERADO, EstadoComprobante.FIRMADO, EstadoComprobante.ENVIADO, EstadoComprobante.ANULADO));
        allowedTransitions.put(EstadoComprobante.AUTORIZADO, EnumSet.of(EstadoComprobante.ANULADO));
        allowedTransitions.put(EstadoComprobante.ANULADO, EnumSet.noneOf(EstadoComprobante.class));
    }

    public boolean canMove(EstadoComprobante from, EstadoComprobante to) {
        if (from == null || to == null) {
            return false;
        }
        return allowedTransitions.getOrDefault(from, EnumSet.noneOf(EstadoComprobante.class)).contains(to);
    }

    public void assertCanMove(EstadoComprobante from, EstadoComprobante to) {
        if (!canMove(from, to)) {
            throw new IllegalStateException("Transicion de estado no permitida: " + from + " -> " + to);
        }
    }
}
