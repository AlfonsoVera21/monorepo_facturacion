package com.factuec.application.port.out;

import com.factuec.infrastructure.persistence.entity.ComprobanteEntity;

public interface EmailPort {
    void sendComprobante(ComprobanteEntity comprobante, byte[] ridePdf);
}
