package com.factuec.application.port.out;

import com.factuec.infrastructure.signature.FirmaConfig;

public interface SignaturePort {
    String firmarXml(String xml, FirmaConfig config);
}
