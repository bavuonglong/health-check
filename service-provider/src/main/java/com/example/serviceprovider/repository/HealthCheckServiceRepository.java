package com.example.serviceprovider.repository;

import com.example.serviceprovider.repository.model.ServiceHealthDomain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HealthCheckServiceRepository extends JpaRepository<ServiceHealthDomain, Long> {
}
