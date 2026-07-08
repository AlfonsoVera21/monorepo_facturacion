package com.factuec;

import com.factuec.config.FactuEcProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(FactuEcProperties.class)
public class FactuEcApplication {

    public static void main(String[] args) {
        SpringApplication.run(FactuEcApplication.class, args);
    }
}
