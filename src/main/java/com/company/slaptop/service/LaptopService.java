package com.company.slaptop.service;

import com.company.slaptop.dto.LaptopRequest;
import com.company.slaptop.dto.LaptopResponse;
import com.company.slaptop.entity.HardwareComponent;
import com.company.slaptop.entity.Laptop;
import com.company.slaptop.entity.Sede;
import com.company.slaptop.entity.SoftwareApplication;
import com.company.slaptop.entity.Usuario;
import com.company.slaptop.exception.ResourceNotFoundException;
import com.company.slaptop.repository.HardwareComponentRepository;
import com.company.slaptop.repository.LaptopRepository;
import com.company.slaptop.repository.SedeRepository;
import com.company.slaptop.repository.SoftwareApplicationRepository;
import com.company.slaptop.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LaptopService {

    private final LaptopRepository laptopRepository;
    private final SedeRepository sedeRepository;
    private final UsuarioRepository usuarioRepository;
    private final HardwareComponentRepository componentRepository;
    private final SoftwareApplicationRepository applicationRepository;

    @Transactional
    public LaptopResponse create(LaptopRequest request) {
        if (laptopRepository.existsByIp(request.ip())) {
            throw new IllegalArgumentException("Ya existe una laptop con la IP " + request.ip());
        }

        Laptop laptop = new Laptop();
        laptop.setIp(request.ip());
        laptop.setNombre(request.nombre());
        laptop.setMarca(request.marca());
        laptop.setModelo(request.modelo());
        laptop.setEstado(request.estado());
        laptop.setSede(findById(sedeRepository, request.sedeId(), "Sede"));
        if (request.usuarioAsignadoId() != null) {
            laptop.setUsuarioAsignado(findById(usuarioRepository, request.usuarioAsignadoId(), "Usuario"));
        }
        laptop.setComponentes(fetchAll(
                componentRepository, request.componenteIds(), "Componente", HardwareComponent::getId));
        laptop.setAplicaciones(fetchAll(
                applicationRepository, request.aplicacionIds(), "Aplicación", SoftwareApplication::getId));
        return toResponse(laptopRepository.save(laptop));
    }

    @Transactional(readOnly = true)
    public List<LaptopResponse> findAll() {
        return laptopRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public LaptopResponse findById(Long id) {
        return toResponse(findById(laptopRepository, id, "Laptop"));
    }

    private LaptopResponse toResponse(Laptop laptop) {
        var site = new LaptopResponse.SedeResponse(laptop.getSede().getId(), laptop.getSede().getNombre());
        var assignedUser = laptop.getUsuarioAsignado() == null ? null :
                new LaptopResponse.UsuarioResponse(
                        laptop.getUsuarioAsignado().getId(),
                        laptop.getUsuarioAsignado().getNombre(),
                        laptop.getUsuarioAsignado().getCorreo()
                );
        var components = laptop.getComponentes().stream()
                .map(component -> new LaptopResponse.ComponentResponse(
                        component.getId(), component.getNombre(), component.getTipo()))
                .toList();
        var applications = laptop.getAplicaciones().stream()
                .map(application -> new LaptopResponse.ApplicationResponse(
                        application.getId(), application.getNombre(), application.getVersion()))
                .toList();
        return new LaptopResponse(
                laptop.getId(), laptop.getIp(), laptop.getNombre(), laptop.getMarca(),
                laptop.getModelo(), laptop.getEstado(), site, assignedUser, components, applications
        );
    }

    private <T> T findById(org.springframework.data.jpa.repository.JpaRepository<T, Long> repository,
                           Long id, String resourceName) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(resourceName + " no encontrado: " + id));
    }

    private <T> Set<T> fetchAll(org.springframework.data.jpa.repository.JpaRepository<T, Long> repository,
                                List<Long> ids, String resourceName,
                                java.util.function.Function<T, Long> idExtractor) {
        List<Long> uniqueIds = ids.stream().distinct().toList();
        List<T> resources = repository.findAllById(uniqueIds);
        if (resources.size() != uniqueIds.size()) {
            Set<Long> foundIds = resources.stream().map(idExtractor).collect(Collectors.toSet());
            Long missingId = uniqueIds.stream().filter(id -> !foundIds.contains(id)).findFirst().orElseThrow();
            throw new ResourceNotFoundException(resourceName + " no encontrado: " + missingId);
        }
        return resources.stream().collect(Collectors.toSet());
    }
}
