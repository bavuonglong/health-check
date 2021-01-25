package com.example.poller.service;

import com.example.poller.repository.model.ServiceHealthDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@Slf4j
public class RestClient {

    private RestTemplate restTemplate;

    public RestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> getServiceStatus(ServiceHealthDomain serviceHealthDomain) {
        URI uri = UriComponentsBuilder.fromUriString(serviceHealthDomain.getUrl()).build().toUri();
        return restTemplate.getForEntity(uri, String.class);
    }
}
