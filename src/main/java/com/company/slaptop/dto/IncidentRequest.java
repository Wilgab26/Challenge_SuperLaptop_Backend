package com.company.slaptop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record IncidentRequest(
        @NotNull Long laptopId,
        LocalDateTime fecha,
        @NotBlank @Size(max = 1000) String descripcion,
        @NotEmpty List<@NotNull Long> componenteDanadoIds
) {
}
