package com.company.slaptop.service;

import com.company.slaptop.dto.CatalogRequest;
import com.company.slaptop.dto.CatalogResponse;
import com.company.slaptop.entity.HardwareComponent;
import com.company.slaptop.entity.Sede;
import com.company.slaptop.entity.SoftwareApplication;
import com.company.slaptop.entity.Usuario;
import com.company.slaptop.exception.ResourceNotFoundException;
import com.company.slaptop.repository.HardwareComponentRepository;
import com.company.slaptop.repository.SedeRepository;
import com.company.slaptop.repository.SoftwareApplicationRepository;
import com.company.slaptop.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final SedeRepository sedeRepository;
    private final UsuarioRepository usuarioRepository;
    private final HardwareComponentRepository componentRepository;
    private final SoftwareApplicationRepository applicationRepository;

    @Transactional
    public CatalogResponse.Sede createSede(CatalogRequest.Sede request) {
        Sede sede = new Sede();
        sede.setNombre(request.nombre());
        sede = sedeRepository.save(sede);
        return new CatalogResponse.Sede(sede.getId(), sede.getNombre());
    }

    @Transactional(readOnly = true)
    public List<CatalogResponse.Sede> findSedes() {
        return sedeRepository.findAll().stream()
                .map(sede -> new CatalogResponse.Sede(sede.getId(), sede.getNombre()))
                .toList();
    }

    @Transactional(readOnly = true)
    public CatalogResponse.Sede findSede(Long id) {
        Sede sede = sedeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sede no encontrada: " + id));
        return new CatalogResponse.Sede(sede.getId(), sede.getNombre());
    }

    @Transactional
    public CatalogResponse.Usuario createUsuario(CatalogRequest.Usuario request) {
        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setCorreo(request.correo());
        usuario = usuarioRepository.save(usuario);
        return new CatalogResponse.Usuario(usuario.getId(), usuario.getNombre(), usuario.getCorreo());
    }

    @Transactional(readOnly = true)
    public List<CatalogResponse.Usuario> findUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(usuario -> new CatalogResponse.Usuario(
                        usuario.getId(), usuario.getNombre(), usuario.getCorreo()))
                .toList();
    }

    @Transactional(readOnly = true)
    public CatalogResponse.Usuario findUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + id));
        return new CatalogResponse.Usuario(usuario.getId(), usuario.getNombre(), usuario.getCorreo());
    }

    @Transactional
    public CatalogResponse.HardwareComponent createComponent(CatalogRequest.HardwareComponent request) {
        HardwareComponent component = new HardwareComponent();
        component.setNombre(request.nombre());
        component.setTipo(request.tipo());
        component = componentRepository.save(component);
        return new CatalogResponse.HardwareComponent(
                component.getId(), component.getNombre(), component.getTipo());
    }

    @Transactional(readOnly = true)
    public List<CatalogResponse.HardwareComponent> findComponents() {
        return componentRepository.findAll().stream()
                .map(component -> new CatalogResponse.HardwareComponent(
                        component.getId(), component.getNombre(), component.getTipo()))
                .toList();
    }

    @Transactional(readOnly = true)
    public CatalogResponse.HardwareComponent findComponent(Long id) {
        HardwareComponent component = componentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Componente no encontrado: " + id));
        return new CatalogResponse.HardwareComponent(
                component.getId(), component.getNombre(), component.getTipo());
    }

    @Transactional
    public CatalogResponse.SoftwareApplication createApplication(CatalogRequest.SoftwareApplication request) {
        SoftwareApplication application = new SoftwareApplication();
        application.setNombre(request.nombre());
        application.setVersion(request.version());
        application = applicationRepository.save(application);
        return new CatalogResponse.SoftwareApplication(
                application.getId(), application.getNombre(), application.getVersion());
    }

    @Transactional(readOnly = true)
    public List<CatalogResponse.SoftwareApplication> findApplications() {
        return applicationRepository.findAll().stream()
                .map(application -> new CatalogResponse.SoftwareApplication(
                        application.getId(), application.getNombre(), application.getVersion()))
                .toList();
    }

    @Transactional(readOnly = true)
    public CatalogResponse.SoftwareApplication findApplication(Long id) {
        SoftwareApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aplicación no encontrada: " + id));
        return new CatalogResponse.SoftwareApplication(
                application.getId(), application.getNombre(), application.getVersion());
    }
}
