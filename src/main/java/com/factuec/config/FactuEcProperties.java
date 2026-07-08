package com.factuec.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "factuec")
public record FactuEcProperties(
        Jwt jwt,
        Sri sri,
        Storage storage,
        Signature signature,
        Soap soap,
        Mail mail,
        Bootstrap bootstrap
) {
    public record Jwt(String secret, Duration accessTokenTtl, Duration refreshTokenTtl) {
    }

    public record Sri(String ambienteDefault, String recepcionPruebasUrl, String autorizacionPruebasUrl,
                      String recepcionProduccionUrl, String autorizacionProduccionUrl, boolean mockEnabled) {
    }

    public record Storage(String xmlPath, String pdfPath, String certificatesPath) {
    }

    public record Signature(boolean mockEnabled) {
    }

    public record Soap(Duration connectTimeout, Duration readTimeout, int retries) {
    }

    public record Mail(boolean enabled, String from) {
    }

    public record Bootstrap(boolean adminEnabled, String adminUsername, String adminEmail, String adminPassword) {
    }
}
