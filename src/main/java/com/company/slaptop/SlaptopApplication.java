package com.company.slaptop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
        title = "SuperLaptop API",
        version = "1.0",
        description = "Administración de laptops, incidentes y fallas de componentes."
))
public class SlaptopApplication {

    public static void main(String[] args) {
        SpringApplication.run(SlaptopApplication.class, args);
    }
}
