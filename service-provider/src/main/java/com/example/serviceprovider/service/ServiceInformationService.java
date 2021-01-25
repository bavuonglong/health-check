package com.example.serviceprovider.service;

import com.example.serviceprovider.constant.ErrorCode;
import com.example.serviceprovider.constant.ServiceStatus;
import com.example.serviceprovider.dto.ServiceInformation;
import com.example.serviceprovider.exception.DuplicatedUrlException;
import com.example.serviceprovider.exception.InvalidArgumentException;
import com.example.serviceprovider.exception.ResourceNotFoundException;
import com.example.serviceprovider.mapper.ServiceHealthMapper;
import com.example.serviceprovider.repository.HealthCheckServiceRepository;
import com.example.serviceprovider.repository.model.ServiceHealthDomain;
import com.example.serviceprovider.util.URLValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ServiceInformationService {

    private HealthCheckServiceRepository repository;

    private ServiceHealthMapper mapper;

    private RestClient restClient;

    public ServiceInformationService(HealthCheckServiceRepository repository, ServiceHealthMapper mapper, RestClient restClient) {
        this.repository = repository;
        this.mapper = mapper;
        this.restClient = restClient;
    }

    public List<ServiceInformation> getAllServices() {
        List<ServiceHealthDomain> serviceHealthDomains = repository.findAll();

        return serviceHealthDomains.stream()
                .map(serviceHealthDomain -> mapper.toDto(serviceHealthDomain))
                .collect(Collectors.toList());
    }

    public ServiceInformation createService(ServiceInformation serviceInformation) {
        if (!URLValidator.isValidUrl(serviceInformation.getUrl())) {
            throw new InvalidArgumentException(HttpStatus.BAD_REQUEST, ErrorCode.SEV_404_001.getValue(), "URL [" + serviceInformation.getUrl() + "] is invalid");
        }

        serviceInformation.setStatus(getServiceHealth(serviceInformation).name());
        try {
            ServiceHealthDomain serviceHealthDomain = repository.save(mapper.toEntity(serviceInformation));
            return mapper.toDto(serviceHealthDomain);
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicatedUrlException(HttpStatus.BAD_REQUEST, ErrorCode.SEV_400_002.getValue(), "URL [" + serviceInformation.getUrl() + "] already exists");
        }
    }

    public ServiceInformation getServiceById(long serviceId) {
        Optional<ServiceHealthDomain> serviceHealthDomainOptional = repository.findById(serviceId);
        ServiceHealthDomain serviceHealthDomain = serviceHealthDomainOptional
                .orElseThrow(() -> new ResourceNotFoundException(HttpStatus.NOT_FOUND, ErrorCode.SEV_404_001.getValue(), "Service with id [" + serviceId + "] is not found"));
        return mapper.toDto(serviceHealthDomain);
    }

    public ServiceInformation updateService(ServiceInformation serviceInformation) {
        if (!URLValidator.isValidUrl(serviceInformation.getUrl())) {
            throw new InvalidArgumentException(HttpStatus.BAD_REQUEST, ErrorCode.SEV_404_001.getValue(), "URL [" + serviceInformation.getUrl() + "] is invalid");
        }

        long serviceId = serviceInformation.getId();
        Optional<ServiceHealthDomain> serviceHealthDomainOptional = repository.findById(serviceId);
        ServiceHealthDomain serviceHealthDomain = serviceHealthDomainOptional
                .orElseThrow(() -> new ResourceNotFoundException(HttpStatus.NOT_FOUND, ErrorCode.SEV_404_001.getValue(), "Service with id [" + serviceId + "] is not found"));

        serviceHealthDomain.setName(serviceInformation.getName());
        serviceHealthDomain.setUrl(serviceInformation.getUrl());
        serviceHealthDomain.setStatus(getServiceHealth(serviceInformation).name());

        repository.save(serviceHealthDomain);

        return mapper.toDto(serviceHealthDomain);
    }

    private ServiceStatus getServiceHealth(ServiceInformation serviceInformation) {
        ServiceStatus status;
        try {
            ResponseEntity<String> responseEntity = restClient.getServiceStatus(serviceInformation.getUrl());
            status = responseEntity.getStatusCode().is2xxSuccessful() ? ServiceStatus.OK : ServiceStatus.FAIL;
            log.debug("Service [{}] has status [{}]", serviceInformation.getName(), status);
        } catch (Exception ex) {
            log.error("Error when fetching status of service [{}], with the exception: [{}]", serviceInformation.getName(), ex.getMessage());
            status = ServiceStatus.FAIL;
        }
        return status;
    }

    public void deleteServiceById(Long serviceId) {
        Optional<ServiceHealthDomain> serviceHealthDomainOptional = repository.findById(serviceId);
        ServiceHealthDomain serviceHealthDomain = serviceHealthDomainOptional
                .orElseThrow(() -> new ResourceNotFoundException(HttpStatus.NOT_FOUND, ErrorCode.SEV_404_001.getValue(), "Service with id [" + serviceId + "] is not found"));

        repository.delete(serviceHealthDomain);
    }
}
