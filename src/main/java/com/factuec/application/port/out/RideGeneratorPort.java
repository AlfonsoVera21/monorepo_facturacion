package com.factuec.application.port.out;

import com.factuec.infrastructure.persistence.entity.ComprobanteEntity;

public interface RideGeneratorPort {
    byte[] generateRide(ComprobanteEntity comprobante);
}
