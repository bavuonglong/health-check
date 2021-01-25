package com.example.poller.service;

import com.example.poller.constant.ServiceStatus;
import com.example.poller.repository.HealthCheckServiceRepository;
import com.example.poller.repository.model.ServiceHealthDomain;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HealthCheckTaskTest {

    private HealthCheckTask healthCheckTask;

    @Mock
    private HealthCheckServiceRepository repository;

    @Mock
    private RestClient restClient;

    @Before
    public void setUp() throws Exception {
        this.healthCheckTask = new HealthCheckTask(repository, restClient);
    }

    @Test
    public void testUpdateServiceHealthStatus() throws Exception {
        ServiceHealthDomain serviceHealthDomain = generateServiceHealthDomain();

        when(repository.findAll()).thenReturn(Collections.singletonList(serviceHealthDomain));
        when(restClient.getServiceStatus(any())).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        assertDoesNotThrow(() -> healthCheckTask.updateServiceHealthStatus());
    }

    private ServiceHealthDomain generateServiceHealthDomain() {
        ServiceHealthDomain serviceHealthDomain = new ServiceHealthDomain();
        serviceHealthDomain.setId(1L);
        serviceHealthDomain.setName("Google");
        serviceHealthDomain.setUrl("https://www.google.com");
        serviceHealthDomain.setStatus(ServiceStatus.OK.name());
        serviceHealthDomain.setDateCreated(new Date());
        serviceHealthDomain.setLastUpdated(new Date());

        return serviceHealthDomain;
    }
}