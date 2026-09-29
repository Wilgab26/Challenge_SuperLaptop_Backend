package com.company.slaptop.dto;

import java.time.LocalDateTime;
import java.util.List;

public record IncidentResponse(
        Long id,
        LocalDateTime fecha,
        String descripcion,
        Long laptopId,
        List<LaptopResponse.ComponentResponse> componentesDanados
) {
}
