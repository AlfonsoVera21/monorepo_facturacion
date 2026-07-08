package com.factuec.application.port.out;

import com.factuec.domain.enums.AmbienteSri;
import com.factuec.infrastructure.sri.SriReceptionResult;

public interface SriReceptionPort {
    SriReceptionResult enviarComprobante(AmbienteSri ambiente, String xmlFirmado);
}
