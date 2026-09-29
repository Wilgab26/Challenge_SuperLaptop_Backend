package com.company.slaptop.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class CatalogRequest {

    private CatalogRequest() {
    }

    public record Sede(
            @NotBlank @Size(max = 100) String nombre
    ) {
    }

    public record Usuario(
            @NotBlank @Size(max = 100) String nombre,
            @NotBlank @Email @Size(max = 150) String correo
    ) {
    }

    public record HardwareComponent(
            @NotBlank @Size(max = 100) String nombre,
            @NotBlank @Size(max = 80) String tipo
    ) {
    }

    public record SoftwareApplication(
            @NotBlank @Size(max = 120) String nombre,
            @NotBlank @Size(max = 60) String version
    ) {
    }
}
