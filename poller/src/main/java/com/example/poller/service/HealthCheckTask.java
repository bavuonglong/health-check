package com.example.poller.service;

import com.example.poller.constant.ServiceStatus;
import com.example.poller.repository.HealthCheckServiceRepository;
import com.example.poller.repository.model.ServiceHealthDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@EnableScheduling
@Slf4j
public class HealthCheckTask {

    private HealthCheckServiceRepository repository;

    private RestClient restClient;

    public HealthCheckTask(HealthCheckServiceRepository repository, RestClient restClient) {
        this.repository = repository;
        this.restClient = restClient;
    }

    @Scheduled(fixedRateString  = "${service.poller.interval}")
    public void updateServiceHealthStatus() {
        log.debug("Start updating the services status at [{}]", new Date());
        List<ServiceHealthDomain> serviceHealthDomainList = repository.findAll();

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                serviceHealthDomainList.stream()
                        .map(this::updateServiceHealth)
                        .toArray(CompletableFuture[]::new)
        );

        allFutures.join();
        log.debug("End updating the services status at [{}]", new Date());
    }

    private CompletableFuture<ServiceHealthDomain> updateServiceHealth(ServiceHealthDomain serviceHealthDomain) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ResponseEntity<String> responseEntity = restClient.getServiceStatus(serviceHealthDomain);
                ServiceStatus status = responseEntity.getStatusCode().is2xxSuccessful() ? ServiceStatus.OK : ServiceStatus.FAIL;
                log.debug("Service [{}] has status [{}]", serviceHealthDomain.getName(), status);
                serviceHealthDomain.setStatus(status.name());
            } catch (Exception ex) {
                log.error("Error when fetching status of service [{}], with the exception: [{}]", serviceHealthDomain.getName(), ex.getMessage());
                serviceHealthDomain.setStatus(ServiceStatus.FAIL.name());
            }
            return serviceHealthDomain;
        }).thenApply(
                newServiceHealthDomain -> repository.save(newServiceHealthDomain)
        );
    }
}
