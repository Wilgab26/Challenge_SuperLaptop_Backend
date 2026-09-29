package com.company.slaptop.repository;

import com.company.slaptop.entity.HardwareComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HardwareComponentRepository extends JpaRepository<HardwareComponent, Long> {

    @Query("""
            select component.id as componentId, component.nombre as componentName,
                   component.tipo as componentType, count(damaged.id) as failureCount
            from DamagedComponent damaged
            join damaged.hardwareComponent component
            group by component.id, component.nombre, component.tipo
            order by count(damaged.id) desc
            """)
    List<ComponentFailureReport> findFailureReport();

    interface ComponentFailureReport {
        Long getComponentId();
        String getComponentName();
        String getComponentType();
        Long getFailureCount();
    }
}
