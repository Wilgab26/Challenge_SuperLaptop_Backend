package com.company.slaptop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "laptops")
@Getter
@Setter
@NoArgsConstructor
public class Laptop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 45)
    private String ip;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 80)
    private String marca;

    @Column(nullable = false, length = 100)
    private String modelo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private LaptopStatus estado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sede_id", nullable = false)
    private Sede sede;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuarioAsignado;

    @ManyToMany
    @JoinTable(
            name = "laptop_hardware_components",
            joinColumns = @JoinColumn(name = "laptop_id"),
            inverseJoinColumns = @JoinColumn(name = "hardware_component_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"laptop_id", "hardware_component_id"})
    )
    private Set<HardwareComponent> componentes = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "laptop_software_applications",
            joinColumns = @JoinColumn(name = "laptop_id"),
            inverseJoinColumns = @JoinColumn(name = "software_application_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"laptop_id", "software_application_id"})
    )
    private Set<SoftwareApplication> aplicaciones = new HashSet<>();
}
