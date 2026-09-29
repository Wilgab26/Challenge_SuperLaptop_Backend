package com.company.slaptop.service;

import com.company.slaptop.entity.DamagedComponent;
import com.company.slaptop.entity.HardwareComponent;
import com.company.slaptop.entity.Incident;
import com.company.slaptop.entity.Laptop;
import com.company.slaptop.entity.LaptopStatus;
import com.company.slaptop.entity.Sede;
import com.company.slaptop.exception.ResourceNotFoundException;
import com.company.slaptop.repository.HardwareComponentRepository;
import com.company.slaptop.repository.IncidentRepository;
import com.company.slaptop.repository.LaptopRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private LaptopRepository laptopRepository;

    @Mock
    private HardwareComponentRepository componentRepository;

    @InjectMocks
    private IncidentService incidentService;

    private Laptop laptop;
    private HardwareComponent component;

    @BeforeEach
    void setUp() {
        Sede sede = new Sede();
        sede.setId(1L);
        sede.setNombre("Lima");

        laptop = new Laptop();
        laptop.setId(10L);
        laptop.setIp("192.168.1.10");
        laptop.setNombre("SL-10");
        laptop.setMarca("Lenovo");
        laptop.setModelo("T14");
        laptop.setEstado(LaptopStatus.ACTIVA);
        laptop.setSede(sede);

        component = new HardwareComponent();
        component.setId(3L);
        component.setNombre("SSD");
        component.setTipo("Almacenamiento");
    }

    @Test
    void createsIncidentWithProvidedDateAndDamagedComponents() {
        LocalDateTime date = LocalDateTime.of(2026, 9, 29, 12, 0);
        when(laptopRepository.findById(10L)).thenReturn(Optional.of(laptop));
        when(componentRepository.findAllById(List.of(3L))).thenReturn(List.of(component));
        when(incidentRepository.save(any(Incident.class))).thenAnswer(invocation -> {
            Incident incident = invocation.getArgument(0);
            incident.setId(20L);
            return incident;
        });

        var response = incidentService.create(
                new com.company.slaptop.dto.IncidentRequest(10L, date, "SSD failed", List.of(3L, 3L)));

        assertEquals(20L, response.id());
        assertEquals(date, response.fecha());
        assertEquals(10L, response.laptopId());
        assertEquals(1, response.componentesDanados().size());
        assertEquals("SSD", response.componentesDanados().getFirst().nombre());
    }

    @Test
    void createsIncidentWithCurrentDateWhenNotProvided() {
        when(laptopRepository.findById(10L)).thenReturn(Optional.of(laptop));
        when(componentRepository.findAllById(List.of(3L))).thenReturn(List.of(component));
        when(incidentRepository.save(any(Incident.class))).thenAnswer(invocation -> invocation.getArgument(0));
        LocalDateTime beforeCreate = LocalDateTime.now();

        var response = incidentService.create(
                new com.company.slaptop.dto.IncidentRequest(10L, null, "SSD failed", List.of(3L)));

        assertNotNull(response.fecha());
        assertTrue(!response.fecha().isBefore(beforeCreate));
    }

    @Test
    void missingLaptopThrowsNotFound() {
        when(laptopRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> incidentService.create(
                new com.company.slaptop.dto.IncidentRequest(99L, null, "failure", List.of(3L))));
    }

    @Test
    void missingComponentThrowsNotFound() {
        when(laptopRepository.findById(10L)).thenReturn(Optional.of(laptop));
        when(componentRepository.findAllById(List.of(99L))).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> incidentService.create(
                new com.company.slaptop.dto.IncidentRequest(10L, null, "failure", List.of(99L))));
    }

    @Test
    void findsIncidentById() {
        Incident incident = incident();
        when(incidentRepository.findById(20L)).thenReturn(Optional.of(incident));

        var response = incidentService.findById(20L);

        assertEquals(20L, response.id());
        assertEquals("SSD failed", response.descripcion());
        assertEquals("SSD", response.componentesDanados().getFirst().nombre());
    }

    @Test
    void missingIncidentThrowsNotFound() {
        when(incidentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> incidentService.findById(99L));
    }

    @Test
    void returnsFailureReport() {
        HardwareComponentRepository.ComponentFailureReport row =
                org.mockito.Mockito.mock(HardwareComponentRepository.ComponentFailureReport.class);
        when(row.getComponentId()).thenReturn(3L);
        when(row.getComponentName()).thenReturn("SSD");
        when(row.getComponentType()).thenReturn("Almacenamiento");
        when(row.getFailureCount()).thenReturn(2L);
        when(componentRepository.findFailureReport()).thenReturn(List.of(row));

        var report = incidentService.failureReport();

        assertEquals(1, report.size());
        assertEquals("SSD", report.getFirst().nombre());
        assertEquals(2L, report.getFirst().cantidadFallas());
    }

    private Incident incident() {
        Incident incident = new Incident();
        incident.setId(20L);
        incident.setLaptop(laptop);
        incident.setFecha(LocalDateTime.of(2026, 9, 29, 12, 0));
        incident.setDescripcion("SSD failed");

        DamagedComponent damagedComponent = new DamagedComponent();
        damagedComponent.setIncident(incident);
        damagedComponent.setHardwareComponent(component);
        incident.setComponentesDanados(new ArrayList<>(List.of(damagedComponent)));
        return incident;
    }
}
