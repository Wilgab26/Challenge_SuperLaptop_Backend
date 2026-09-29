package com.company.slaptop.service;

import com.company.slaptop.dto.LaptopRequest;
import com.company.slaptop.entity.HardwareComponent;
import com.company.slaptop.entity.Laptop;
import com.company.slaptop.entity.LaptopStatus;
import com.company.slaptop.entity.Sede;
import com.company.slaptop.entity.SoftwareApplication;
import com.company.slaptop.entity.Usuario;
import com.company.slaptop.exception.ResourceNotFoundException;
import com.company.slaptop.repository.HardwareComponentRepository;
import com.company.slaptop.repository.LaptopRepository;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LaptopServiceTest {

    @Mock
    private LaptopRepository laptopRepository;

    @Mock
    private SedeRepository sedeRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private HardwareComponentRepository componentRepository;

    @Mock
    private SoftwareApplicationRepository applicationRepository;

    @InjectMocks
    private LaptopService laptopService;

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
    void createsLaptopWithAllAssociations() {
        Laptop savedLaptop = laptop(sede, usuario, Set.of(component), Set.of(application));
        when(laptopRepository.existsByIp("192.168.1.10")).thenReturn(false);
        when(sedeRepository.findById(1L)).thenReturn(Optional.of(sede));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario));
        when(componentRepository.findAllById(List.of(3L))).thenReturn(List.of(component));
        when(applicationRepository.findAllById(List.of(4L))).thenReturn(List.of(application));
        when(laptopRepository.save(any(Laptop.class))).thenReturn(savedLaptop);

        var response = laptopService.create(request(2L, List.of(3L), List.of(4L)));

        assertEquals(10L, response.id());
        assertEquals("Lima", response.sede().nombre());
        assertEquals("ana@example.com", response.usuarioAsignado().correo());
        assertEquals("SSD", response.componentes().getFirst().nombre());
        assertEquals("Office", response.aplicaciones().getFirst().nombre());
    }

    @Test
    void createsLaptopWithoutOptionalAssociations() {
        Laptop savedLaptop = laptop(sede, null, Set.of(), Set.of());
        when(laptopRepository.existsByIp("192.168.1.10")).thenReturn(false);
        when(sedeRepository.findById(1L)).thenReturn(Optional.of(sede));
        when(componentRepository.findAllById(List.of())).thenReturn(List.of());
        when(applicationRepository.findAllById(List.of())).thenReturn(List.of());
        when(laptopRepository.save(any(Laptop.class))).thenReturn(savedLaptop);

        var response = laptopService.create(request(null, List.of(), List.of()));

        assertNull(response.usuarioAsignado());
        assertEquals(0, response.componentes().size());
        assertEquals(0, response.aplicaciones().size());
    }

    @Test
    void duplicateIpThrowsConflict() {
        when(laptopRepository.existsByIp("192.168.1.10")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> laptopService.create(request(null, List.of(), List.of())));
    }

    @Test
    void missingSiteThrowsNotFound() {
        when(laptopRepository.existsByIp("192.168.1.10")).thenReturn(false);
        when(sedeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> laptopService.create(request(null, List.of(), List.of())));
    }

    @Test
    void missingAssignedUserThrowsNotFound() {
        when(laptopRepository.existsByIp("192.168.1.10")).thenReturn(false);
        when(sedeRepository.findById(1L)).thenReturn(Optional.of(sede));
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> laptopService.create(request(99L, List.of(), List.of())));
    }

    @Test
    void missingHardwareComponentThrowsNotFound() {
        when(laptopRepository.existsByIp("192.168.1.10")).thenReturn(false);
        when(sedeRepository.findById(1L)).thenReturn(Optional.of(sede));
        when(componentRepository.findAllById(List.of(99L))).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class,
                () -> laptopService.create(request(null, List.of(99L), List.of())));
    }

    @Test
    void missingApplicationThrowsNotFound() {
        when(laptopRepository.existsByIp("192.168.1.10")).thenReturn(false);
        when(sedeRepository.findById(1L)).thenReturn(Optional.of(sede));
        when(componentRepository.findAllById(List.of())).thenReturn(List.of());
        when(applicationRepository.findAllById(List.of(99L))).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class,
                () -> laptopService.create(request(null, List.of(), List.of(99L))));
    }

    @Test
    void duplicateAssociationIdsAreDeduplicated() {
        Laptop savedLaptop = laptop(sede, null, Set.of(component), Set.of());
        when(laptopRepository.existsByIp("192.168.1.10")).thenReturn(false);
        when(sedeRepository.findById(1L)).thenReturn(Optional.of(sede));
        when(componentRepository.findAllById(List.of(3L))).thenReturn(List.of(component));
        when(applicationRepository.findAllById(List.of())).thenReturn(List.of());
        when(laptopRepository.save(any(Laptop.class))).thenReturn(savedLaptop);

        var response = laptopService.create(request(null, List.of(3L, 3L), List.of()));

        assertEquals(1, response.componentes().size());
    }

    @Test
    void listsAndFindsLaptops() {
        Laptop storedLaptop = laptop(sede, null, Set.of(), Set.of());
        when(laptopRepository.findAll()).thenReturn(List.of(storedLaptop));
        when(laptopRepository.findById(10L)).thenReturn(Optional.of(storedLaptop));

        assertEquals(1, laptopService.findAll().size());
        assertEquals(10L, laptopService.findById(10L).id());
    }

    @Test
    void missingLaptopThrowsNotFound() {
        when(laptopRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> laptopService.findById(99L));
    }

    private LaptopRequest request(Long userId, List<Long> componentIds, List<Long> applicationIds) {
        return new LaptopRequest(
                "192.168.1.10", "SL-10", "Lenovo", "T14", LaptopStatus.ACTIVA,
                1L, userId, componentIds, applicationIds
        );
    }

    private Laptop laptop(Sede site, Usuario assignedUser,
                          Set<HardwareComponent> components, Set<SoftwareApplication> applications) {
        Laptop laptop = new Laptop();
        laptop.setId(10L);
        laptop.setIp("192.168.1.10");
        laptop.setNombre("SL-10");
        laptop.setMarca("Lenovo");
        laptop.setModelo("T14");
        laptop.setEstado(LaptopStatus.ACTIVA);
        laptop.setSede(site);
        laptop.setUsuarioAsignado(assignedUser);
        laptop.setComponentes(components);
        laptop.setAplicaciones(applications);
        return laptop;
    }
}
