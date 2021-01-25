package com.example.serviceprovider.controller;

import com.example.serviceprovider.constant.SuccessCode;
import com.example.serviceprovider.dto.ServiceInformation;
import com.example.serviceprovider.dto.ServiceResponse;
import com.example.serviceprovider.service.ServiceInformationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/services")
public class ServiceInformationController {

    private ServiceInformationService service;

    public ServiceInformationController(ServiceInformationService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ServiceResponse> getAllServices() {
        return ResponseEntity.ok(ServiceResponse.builder()
                .code(SuccessCode.SEV_200.getValue())
                .data(service.getAllServices())
                .build()
        );
    }

    @PostMapping
    public ResponseEntity<ServiceResponse> createService(@RequestBody ServiceInformation serviceInformation) {
        serviceInformation.setId(null);
        ServiceInformation serviceInformationFromDB = service.createService(serviceInformation);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/" + serviceInformationFromDB.getId().toString())
                .build()
                .toUri();
        return ResponseEntity.status(HttpStatus.CREATED).location(location).body(
                ServiceResponse.builder()
                        .code(SuccessCode.SEV_201.getValue())
                        .data(serviceInformationFromDB)
                        .build()
        );
    }

    @GetMapping("/{service-id}")
    public ResponseEntity<ServiceResponse> getServiceById(@PathVariable(value = "service-id") Long serviceId) {
        ServiceInformation serviceInformationFromDB = service.getServiceById(serviceId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ServiceResponse.builder()
                        .code(SuccessCode.SEV_200.getValue())
                        .data(serviceInformationFromDB)
                        .build()
        );
    }

    @PutMapping
    public ResponseEntity<ServiceResponse> updateService(@RequestBody ServiceInformation serviceInformation) {
        ServiceInformation serviceInformationFromDB = service.updateService(serviceInformation);
        return ResponseEntity.status(HttpStatus.OK).body(
                ServiceResponse.builder()
                        .code(SuccessCode.SEV_200.getValue())
                        .data(serviceInformationFromDB)
                        .build()
        );
    }

    @DeleteMapping("/{service-id}")
    public ResponseEntity<ServiceResponse> deleteServiceById(@PathVariable(value = "service-id") Long serviceId) {
        service.deleteServiceById(serviceId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ServiceResponse.builder()
                        .code(SuccessCode.SEV_200.getValue())
                        .build()
        );
    }
}
