package com.company.slaptop.dto;

public final class CatalogResponse {

    private CatalogResponse() {
    }

    public record Sede(Long id, String nombre) {
    }

    public record Usuario(Long id, String nombre, String correo) {
    }

    public record HardwareComponent(Long id, String nombre, String tipo) {
    }

    public record SoftwareApplication(Long id, String nombre, String version) {
    }
}
