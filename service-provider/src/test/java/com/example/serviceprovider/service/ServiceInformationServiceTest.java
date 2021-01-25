package com.example.serviceprovider.service;

import com.example.serviceprovider.constant.ServiceStatus;
import com.example.serviceprovider.dto.ServiceInformation;
import com.example.serviceprovider.mapper.ServiceHealthMapper;
import com.example.serviceprovider.repository.HealthCheckServiceRepository;
import com.example.serviceprovider.repository.model.ServiceHealthDomain;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ServiceInformationServiceTest {

    private ServiceInformationService serviceInformationService;

    @Mock
    private HealthCheckServiceRepository healthCheckServiceRepository;

    @Mock
    private ServiceHealthMapper mapper;

    @Mock
    private RestClient restClient;

    @Before
    public void setUp() {
        this.serviceInformationService = new ServiceInformationService(healthCheckServiceRepository, mapper, restClient);
    }

    @Test
    public void givenServiceInDB_whenGetAllServices_thenReturnThatService() {
        ServiceHealthDomain serviceHealthDomain = generateServiceHealthDomain();
        ServiceInformation serviceInformation = generateServiceInformation();

        when(healthCheckServiceRepository.findAll()).thenReturn(Collections.singletonList(serviceHealthDomain));
        when(mapper.toDto(serviceHealthDomain)).thenReturn(serviceInformation);

        List<ServiceInformation> allServices = serviceInformationService.getAllServices();

        assertEquals(1, allServices.size());
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

    private ServiceInformation generateServiceInformation() {
        ServiceInformation serviceInformation = new ServiceInformation();
        serviceInformation.setId(1L);
        serviceInformation.setName("Google");
        serviceInformation.setUrl("https://www.google.com");
        serviceInformation.setStatus(ServiceStatus.OK.name());
        serviceInformation.setDateCreated(new Date());
        serviceInformation.setLastUpdated(new Date());

        return serviceInformation;
    }

    @Test
    public void givenValidService_whenCreateService_thenReturnNewServiceInformation() throws Exception {
        ServiceHealthDomain serviceHealthDomain = generateServiceHealthDomain();
        ServiceInformation serviceInformation = generateServiceInformation();

        when(healthCheckServiceRepository.save(any())).thenReturn(serviceHealthDomain);
        when(restClient.getServiceStatus(anyString())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(mapper.toDto(serviceHealthDomain)).thenReturn(serviceInformation);

        ServiceInformation newServiceInformation = serviceInformationService.createService(serviceInformation);

        assertEquals(serviceInformation, newServiceInformation);
    }

    @Test
    public void givenValidServiceId_whenGetServiceById_thenReturnThatServiceFromDB() {
        ServiceHealthDomain serviceHealthDomain = generateServiceHealthDomain();
        ServiceInformation serviceInformation = generateServiceInformation();

        when(healthCheckServiceRepository.findById(any())).thenReturn(Optional.of(serviceHealthDomain));
        when(mapper.toDto(serviceHealthDomain)).thenReturn(serviceInformation);

        ServiceInformation serviceById = serviceInformationService.getServiceById(anyLong());

        assertEquals(serviceInformation, serviceById);
    }

    @Test
    public void givenValidServiceId_whenUpdateService_thenReturnUpdatedService() throws Exception {
        ServiceHealthDomain serviceHealthDomain = generateServiceHealthDomain();
        ServiceInformation serviceInformation = generateServiceInformation();

        when(healthCheckServiceRepository.findById(anyLong())).thenReturn(Optional.of(serviceHealthDomain));
        when(restClient.getServiceStatus(anyString())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(mapper.toDto(serviceHealthDomain)).thenReturn(serviceInformation);

        ServiceInformation updatedService = serviceInformationService.updateService(serviceInformation);

        assertEquals(serviceInformation, updatedService);
    }

    @Test
    public void givenValidServiceId_whenDeleteService_thenReturnNothingWithoutException() {
        ServiceHealthDomain serviceHealthDomain = generateServiceHealthDomain();

        when(healthCheckServiceRepository.findById(any())).thenReturn(Optional.of(serviceHealthDomain));
        doNothing().when(healthCheckServiceRepository).delete(any());

        assertDoesNotThrow(() -> serviceInformationService.deleteServiceById(1L));
    }
}