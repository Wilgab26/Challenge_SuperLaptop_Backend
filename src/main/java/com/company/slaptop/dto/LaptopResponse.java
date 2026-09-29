package com.company.slaptop.dto;

import com.company.slaptop.entity.LaptopStatus;

import java.util.List;

public record LaptopResponse(
        Long id,
        String ip,
        String nombre,
        String marca,
        String modelo,
        LaptopStatus estado,
        SedeResponse sede,
        UsuarioResponse usuarioAsignado,
        List<ComponentResponse> componentes,
        List<ApplicationResponse> aplicaciones
) {
    public record SedeResponse(Long id, String nombre) {
    }

    public record UsuarioResponse(Long id, String nombre, String correo) {
    }

    public record ComponentResponse(Long id, String nombre, String tipo) {
    }

    public record ApplicationResponse(Long id, String nombre, String version) {
    }
}
