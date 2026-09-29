package com.company.slaptop.dto;

public record ComponentFailureResponse(
        Long componentId,
        String nombre,
        String tipo,
        Long cantidadFallas
) {
}
