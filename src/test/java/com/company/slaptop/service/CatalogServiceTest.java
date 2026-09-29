package com.company.slaptop.service;

import com.company.slaptop.dto.CatalogRequest;
import com.company.slaptop.entity.HardwareComponent;
import com.company.slaptop.entity.Sede;
import com.company.slaptop.entity.SoftwareApplication;
import com.company.slaptop.entity.Usuario;
import com.company.slaptop.exception.ResourceNotFoundException;
import com.company.slaptop.repository.HardwareComponentRepository;
import com.company.slaptop.repository.SedeRepository;
import com.company.slaptop.repository.SoftwareApplicationRepository;
import com.company.slaptop.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @Mock
    private SedeRepository sedeRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private HardwareComponentRepository componentRepository;

    @Mock
    private SoftwareApplicationRepository applicationRepository;

    @InjectMocks
    private CatalogService catalogService;

    private Sede sede;
    private Usuario usuario;
    private HardwareComponent component;
    private SoftwareApplication application;

    @BeforeEach
    void setUp() {
        sede = new Sede();
        sede.setId(1L);
        sede.setNombre("Lima");

        usuario = new Usuario();
        usuario.setId(2L);
        usuario.setNombre("Ana");
        usuario.setCorreo("ana@example.com");

        component = new HardwareComponent();
        component.setId(3L);
        component.setNombre("SSD");
        component.setTipo("Almacenamiento");

        application = new SoftwareApplication();
        application.setId(4L);
        application.setNombre("Office");
        application.setVersion("2024");
    }

    @Test
    void createsAndListsSites() {
        when(sedeRepository.save(any(Sede.class))).thenReturn(sede);
        when(sedeRepository.findAll()).thenReturn(List.of(sede));
        when(sedeRepository.findById(1L)).thenReturn(Optional.of(sede));

        assertEquals("Lima", catalogService.createSede(new CatalogRequest.Sede("Lima")).nombre());
        assertEquals("Lima", catalogService.findSedes().getFirst().nombre());
        assertEquals("Lima", catalogService.findSede(1L).nombre());
    }

    @Test
    void missingSiteThrowsNotFound() {
        when(sedeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> catalogService.findSede(99L));
    }

    @Test
    void createsAndListsUsers() {
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario));

        assertEquals("ana@example.com", catalogService.createUsuario(
                new CatalogRequest.Usuario("Ana", "ana@example.com")).correo());
        assertEquals("Ana", catalogService.findUsuarios().getFirst().nombre());
        assertEquals("Ana", catalogService.findUsuario(2L).nombre());
    }

    @Test
    void missingUserThrowsNotFound() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> catalogService.findUsuario(99L));
    }

    @Test
    void createsAndListsHardwareComponents() {
        when(componentRepository.save(any(HardwareComponent.class))).thenReturn(component);
        when(componentRepository.findAll()).thenReturn(List.of(component));
        when(componentRepository.findById(3L)).thenReturn(Optional.of(component));

        assertEquals("SSD", catalogService.createComponent(
                new CatalogRequest.HardwareComponent("SSD", "Almacenamiento")).nombre());
        assertEquals("SSD", catalogService.findComponents().getFirst().nombre());
        assertEquals("SSD", catalogService.findComponent(3L).nombre());
    }

    @Test
    void missingComponentThrowsNotFound() {
        when(componentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> catalogService.findComponent(99L));
    }

    @Test
    void createsAndListsApplications() {
        when(applicationRepository.save(any(SoftwareApplication.class))).thenReturn(application);
        when(applicationRepository.findAll()).thenReturn(List.of(application));
        when(applicationRepository.findById(4L)).thenReturn(Optional.of(application));

        assertEquals("Office", catalogService.createApplication(
                new CatalogRequest.SoftwareApplication("Office", "2024")).nombre());
        assertEquals("Office", catalogService.findApplications().getFirst().nombre());
        assertEquals("Office", catalogService.findApplication(4L).nombre());
    }

    @Test
    void missingApplicationThrowsNotFound() {
        when(applicationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> catalogService.findApplication(99L));
    }
}
