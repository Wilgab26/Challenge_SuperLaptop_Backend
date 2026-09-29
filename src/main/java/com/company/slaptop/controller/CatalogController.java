package com.company.slaptop.controller;

import com.company.slaptop.dto.CatalogRequest;
import com.company.slaptop.dto.CatalogResponse;
import com.company.slaptop.service.CatalogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;

    @PostMapping("/sedes")
    public ResponseEntity<CatalogResponse.Sede> createSede(@Valid @RequestBody CatalogRequest.Sede request) {
        CatalogResponse.Sede response = catalogService.createSede(request);
        return createdResponse(response.id(), response);
    }

    @GetMapping("/sedes")
    public List<CatalogResponse.Sede> findSedes() {
        return catalogService.findSedes();
    }

    @GetMapping("/sedes/{id}")
    public CatalogResponse.Sede findSede(@PathVariable Long id) {
        return catalogService.findSede(id);
    }

    @PostMapping("/usuarios")
    public ResponseEntity<CatalogResponse.Usuario> createUsuario(
            @Valid @RequestBody CatalogRequest.Usuario request) {
        CatalogResponse.Usuario response = catalogService.createUsuario(request);
        return createdResponse(response.id(), response);
    }

    @GetMapping("/usuarios")
    public List<CatalogResponse.Usuario> findUsuarios() {
        return catalogService.findUsuarios();
    }

    @GetMapping("/usuarios/{id}")
    public CatalogResponse.Usuario findUsuario(@PathVariable Long id) {
        return catalogService.findUsuario(id);
    }

    @PostMapping("/componentes")
    public ResponseEntity<CatalogResponse.HardwareComponent> createComponent(
            @Valid @RequestBody CatalogRequest.HardwareComponent request) {
        CatalogResponse.HardwareComponent response = catalogService.createComponent(request);
        return createdResponse(response.id(), response);
    }

    @GetMapping("/componentes")
    public List<CatalogResponse.HardwareComponent> findComponents() {
        return catalogService.findComponents();
    }

    @GetMapping("/componentes/{id}")
    public CatalogResponse.HardwareComponent findComponent(@PathVariable Long id) {
        return catalogService.findComponent(id);
    }

    @PostMapping("/aplicaciones")
    public ResponseEntity<CatalogResponse.SoftwareApplication> createApplication(
            @Valid @RequestBody CatalogRequest.SoftwareApplication request) {
        CatalogResponse.SoftwareApplication response = catalogService.createApplication(request);
        return createdResponse(response.id(), response);
    }

    @GetMapping("/aplicaciones")
    public List<CatalogResponse.SoftwareApplication> findApplications() {
        return catalogService.findApplications();
    }

    @GetMapping("/aplicaciones/{id}")
    public CatalogResponse.SoftwareApplication findApplication(@PathVariable Long id) {
        return catalogService.findApplication(id);
    }

    private <T> ResponseEntity<T> createdResponse(Long id, T response) {
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(location).body(response);
    }
}
