package com.company.slaptop.service;

import com.company.slaptop.dto.ComponentFailureResponse;
import com.company.slaptop.dto.IncidentRequest;
import com.company.slaptop.dto.IncidentResponse;
import com.company.slaptop.dto.LaptopResponse;
import com.company.slaptop.entity.DamagedComponent;
import com.company.slaptop.entity.Incident;
import com.company.slaptop.exception.ResourceNotFoundException;
import com.company.slaptop.repository.HardwareComponentRepository;
import com.company.slaptop.repository.IncidentRepository;
import com.company.slaptop.repository.LaptopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final LaptopRepository laptopRepository;
    private final HardwareComponentRepository componentRepository;

    @Transactional
    public IncidentResponse create(IncidentRequest request) {
        var laptop = laptopRepository.findById(request.laptopId())
                .orElseThrow(() -> new ResourceNotFoundException("Laptop no encontrada: " + request.laptopId()));
        List<Long> componentIds = request.componenteDanadoIds().stream().distinct().toList();
        var components = componentRepository.findAllById(componentIds);
        if (components.size() != componentIds.size()) {
            var foundIds = components.stream().map(component -> component.getId()).toList();
            Long missingId = componentIds.stream().filter(id -> !foundIds.contains(id)).findFirst().orElseThrow();
            throw new ResourceNotFoundException("Componente no encontrado: " + missingId);
        }

        Incident incident = new Incident();
        incident.setLaptop(laptop);
        incident.setFecha(request.fecha() == null ? LocalDateTime.now() : request.fecha());
        incident.setDescripcion(request.descripcion());
        for (var component : components) {
            DamagedComponent damagedComponent = new DamagedComponent();
            damagedComponent.setIncident(incident);
            damagedComponent.setHardwareComponent(component);
            incident.getComponentesDanados().add(damagedComponent);
        }
        return toResponse(incidentRepository.save(incident));
    }

    @Transactional(readOnly = true)
    public IncidentResponse findById(Long id) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incidente no encontrado: " + id));
        return toResponse(incident);
    }

    @Transactional(readOnly = true)
    public List<ComponentFailureResponse> failureReport() {
        return componentRepository.findFailureReport().stream()
                .map(row -> new ComponentFailureResponse(
                        row.getComponentId(), row.getComponentName(), row.getComponentType(), row.getFailureCount()))
                .toList();
    }

    private IncidentResponse toResponse(Incident incident) {
        var damagedComponents = incident.getComponentesDanados().stream()
                .map(damaged -> new LaptopResponse.ComponentResponse(
                        damaged.getHardwareComponent().getId(),
                        damaged.getHardwareComponent().getNombre(),
                        damaged.getHardwareComponent().getTipo()))
                .toList();
        return new IncidentResponse(
                incident.getId(), incident.getFecha(), incident.getDescripcion(),
                incident.getLaptop().getId(), damagedComponents
        );
    }
}
