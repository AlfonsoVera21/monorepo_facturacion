package com.factuec.infrastructure.signature;

import java.nio.file.Path;
import java.time.LocalDate;

public record FirmaConfig(
        Path certificatePath,
        char[] password,
        LocalDate validUntil,
        boolean mockEnabled
) {
}
