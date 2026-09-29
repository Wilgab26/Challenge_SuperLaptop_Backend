package com.company.slaptop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "damaged_components",
        uniqueConstraints = @UniqueConstraint(columnNames = {"incident_id", "hardware_component_id"})
)
@Getter
@Setter
@NoArgsConstructor
public class DamagedComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "incident_id", nullable = false)
    private Incident incident;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hardware_component_id", nullable = false)
    private HardwareComponent hardwareComponent;
}
