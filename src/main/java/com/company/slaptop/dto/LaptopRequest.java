package com.company.slaptop.dto;

import com.company.slaptop.entity.LaptopStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record LaptopRequest(
        @NotBlank @Size(max = 45) String ip,
        @NotBlank @Size(max = 100) String nombre,
        @NotBlank @Size(max = 80) String marca,
        @NotBlank @Size(max = 100) String modelo,
        @NotNull LaptopStatus estado,
        @NotNull Long sedeId,
        Long usuarioAsignadoId,
        List<@NotNull Long> componenteIds,
        List<@NotNull Long> aplicacionIds
) {
    public LaptopRequest {
        componenteIds = componenteIds == null ? List.of() : List.copyOf(componenteIds);
        aplicacionIds = aplicacionIds == null ? List.of() : List.copyOf(aplicacionIds);
    }
}
