package com.company.slaptop.repository;

import com.company.slaptop.entity.SoftwareApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoftwareApplicationRepository extends JpaRepository<SoftwareApplication, Long> {
}
