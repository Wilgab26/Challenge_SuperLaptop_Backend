package com.company.slaptop.repository;

import com.company.slaptop.entity.Laptop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LaptopRepository extends JpaRepository<Laptop, Long> {
    boolean existsByIp(String ip);
}
