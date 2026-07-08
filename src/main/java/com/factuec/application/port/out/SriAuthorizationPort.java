package com.factuec.application.port.out;

import com.factuec.domain.enums.AmbienteSri;
import com.factuec.infrastructure.sri.SriAuthorizationResult;

public interface SriAuthorizationPort {
    SriAuthorizationResult consultarAutorizacion(AmbienteSri ambiente, String claveAcceso);
}
