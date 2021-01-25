package com.example.poller.repository;

import com.example.poller.repository.model.ServiceHealthDomain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HealthCheckServiceRepository extends JpaRepository<ServiceHealthDomain, Long> {
}
