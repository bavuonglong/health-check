package com.example.serviceprovider.controller;

import com.example.serviceprovider.constant.ErrorCode;
import com.example.serviceprovider.constant.ServiceStatus;
import com.example.serviceprovider.constant.SuccessCode;
import com.example.serviceprovider.controller.advice.GlobalExceptionHandler;
import com.example.serviceprovider.dto.ServiceInformation;
import com.example.serviceprovider.dto.ServiceResponse;
import com.example.serviceprovider.exception.ErrorResponseFactory;
import com.example.serviceprovider.exception.InvalidArgumentException;
import com.example.serviceprovider.exception.ResourceNotFoundException;
import com.example.serviceprovider.service.ServiceInformationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class ServiceInformationControllerTest {

    @InjectMocks
    private ServiceInformationController serviceInformationController;

    @Mock
    private ServiceInformationService serviceInformationService;

    private ObjectMapper mapper;

    @Mock
    private ErrorResponseFactory errorResponseFactory;

    private MockMvc mvc;

    @Before
    public void setUp() {
        this.mvc = MockMvcBuilders.standaloneSetup(this.serviceInformationController)
                .setControllerAdvice(new GlobalExceptionHandler(errorResponseFactory))
                .build();

        mapper = new ObjectMapper();
    }

    @Test
    public void givenValidRequest_whenGetAllServices_thenReturnAllExistingServices() throws Exception {
        // Given
        URI allServices = UriComponentsBuilder.fromUriString("/api/v1/services").build().toUri();

        ServiceInformation googleService = new ServiceInformation();
        googleService.setName("Google");
        googleService.setUrl("https://google.com/");
        googleService.setStatus(ServiceStatus.OK.name());

        ServiceInformation facebookService = new ServiceInformation();
        facebookService.setName("Facebook");
        facebookService.setUrl("https://facebook.com/");
        facebookService.setStatus(ServiceStatus.OK.name());

        ServiceInformation myService = new ServiceInformation();
        myService.setName("MyService");
        myService.setUrl("https://myserivce.com/");
        myService.setStatus(ServiceStatus.FAIL.name());

        List<ServiceInformation> serviceInformations = Arrays.asList(googleService, facebookService, myService);
        when(serviceInformationService.getAllServices()).thenReturn(serviceInformations);

        // When - Then
        mvc.perform(get(allServices)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code", is(SuccessCode.SEV_200.getValue())))
                .andExpect(jsonPath("$.data.length()", is(3)))
                .andExpect(jsonPath("$.data[*].name").value(hasItem("Google")))
                .andExpect(jsonPath("$.data[*].url").value(hasItem("https://google.com/")))
                .andExpect(status().isOk());
    }

    @Test
    public void givenValidServiceDTO_whenCreateService_thenReturnValidServiceURI() throws Exception {
        URI createServiceURL = UriComponentsBuilder.fromUriString("/api/v1/services").build().toUri();

        ServiceInformation googleService = new ServiceInformation();
        googleService.setId(1L);
        googleService.setName("Google");
        googleService.setUrl("https://google.com/");
        googleService.setStatus(ServiceStatus.OK.name());

        when(serviceInformationService.createService(any(ServiceInformation.class))).thenReturn(googleService);

        mvc.perform(post(createServiceURL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(googleService)))
                .andExpect(jsonPath("$.code", is(SuccessCode.SEV_201.getValue())))
                .andExpect(jsonPath("$.data.name", is("Google")))
                .andExpect(status().isCreated());
    }

    @Test
    public void givenServiceWithInvalidURL_whenCreateService_thenReturnInvalidArgumentError() throws Exception {
        ResponseEntity<ServiceResponse> responseEntity = ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ServiceResponse.builder()
                        .code(ErrorCode.SEV_400_001.getValue())
                        .developerMessage("URL [xyz] is invalid")
                        .build());

        when(errorResponseFactory.generateErrorResponse(any(InvalidArgumentException.class))).thenReturn(responseEntity);
        when(serviceInformationService.createService(any(ServiceInformation.class))).thenThrow(new InvalidArgumentException(HttpStatus.BAD_REQUEST, ErrorCode.SEV_404_001.getValue(), "URL [xyz] is invalid"));

        URI createServiceURL = UriComponentsBuilder.fromUriString("/api/v1/services").build().toUri();

        ServiceInformation invalidService = new ServiceInformation();
        invalidService.setId(1L);
        invalidService.setName("Invalid Service");
        invalidService.setUrl("xyz");
        invalidService.setStatus(ServiceStatus.OK.name());

        mvc.perform(post(createServiceURL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(invalidService)))
                .andExpect(jsonPath("$.code", is(ErrorCode.SEV_400_001.getValue())))
                .andExpect(jsonPath("$.developer_message", is("URL [xyz] is invalid")))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void givenValidRequest_whenGetAllById_thenReturnCorrespondingService() throws Exception {
        // Given
        URI serviceByIdURI = UriComponentsBuilder.fromUriString("/api/v1/services").path("/1").build().toUri();

        ServiceInformation googleService = new ServiceInformation();
        googleService.setId(1L);
        googleService.setName("Google");
        googleService.setUrl("https://google.com/");
        googleService.setStatus(ServiceStatus.OK.name());

        when(serviceInformationService.getServiceById(1L)).thenReturn(googleService);

        // When - Then
        mvc.perform(get(serviceByIdURI)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code", is(SuccessCode.SEV_200.getValue())))
                .andExpect(jsonPath("$.data.name", is("Google")))
                .andExpect(status().isOk());
    }

    @Test
    public void givenRequestWithInvalidServiceId_whenGetServiceById_thenReturnResourceNotFoundError() throws Exception {
        // Given
        ResponseEntity<ServiceResponse> responseEntity = ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ServiceResponse.builder()
                        .code(ErrorCode.SEV_404_001.getValue())
                        .developerMessage("Service with id [100] is not found")
                        .build());

        when(errorResponseFactory.generateErrorResponse(any(ResourceNotFoundException.class))).thenReturn(responseEntity);
        when(serviceInformationService.getServiceById(100L)).thenThrow(new ResourceNotFoundException(HttpStatus.NOT_FOUND, ErrorCode.SEV_404_001.getValue(), "Service with id [100] is not found"));

        URI serviceByIdURI = UriComponentsBuilder.fromUriString("/api/v1/services").path("/100").build().toUri();

        // When - Then
        mvc.perform(get(serviceByIdURI)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code", is(ErrorCode.SEV_404_001.getValue())))
                .andExpect(jsonPath("$.developer_message", is("Service with id [100] is not found")))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void givenValidServiceDTO_whenUpdateService_thenReturnUpdatedService() throws Exception {
        URI updateServiceURL = UriComponentsBuilder.fromUriString("/api/v1/services").build().toUri();

        ServiceInformation googleService = new ServiceInformation();
        googleService.setId(1L);
        googleService.setName("Google");
        googleService.setUrl("https://google.com/");
        googleService.setStatus(ServiceStatus.OK.name());

        when(serviceInformationService.updateService(any(ServiceInformation.class))).thenReturn(googleService);

        mvc.perform(put(updateServiceURL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(googleService)))
                .andExpect(jsonPath("$.code", is(SuccessCode.SEV_200.getValue())))
                .andExpect(jsonPath("$.data.name", is("Google")))
                .andExpect(status().isOk());
    }

    @Test
    public void givenValidServiceDTO_whenDeleteService_thenReturnOkStatus() throws Exception {
        URI deleteServiceURL = UriComponentsBuilder.fromUriString("/api/v1/services").path("/1").build().toUri();

        doNothing().when(serviceInformationService).deleteServiceById(any());

        mvc.perform(delete(deleteServiceURL)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code", is(SuccessCode.SEV_200.getValue())))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(status().isOk());
    }

    @Test
    public void givenRequestWithInvalidServiceId_whenDeleteService_thenReturnResourceNotFoundError() throws Exception {
        // Given
        ResponseEntity<ServiceResponse> responseEntity = ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ServiceResponse.builder()
                        .code(ErrorCode.SEV_404_001.getValue())
                        .developerMessage("Service with id [100] is not found")
                        .build());

        when(errorResponseFactory.generateErrorResponse(any(ResourceNotFoundException.class))).thenReturn(responseEntity);

        doThrow(new ResourceNotFoundException(HttpStatus.NOT_FOUND, ErrorCode.SEV_404_001.getValue(), "Service with id [100] is not found")).when(serviceInformationService).deleteServiceById(100L);

        URI deleteServiceByIdURI = UriComponentsBuilder.fromUriString("/api/v1/services").path("/100").build().toUri();

        // When - Then
        mvc.perform(delete(deleteServiceByIdURI)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code", is(ErrorCode.SEV_404_001.getValue())))
                .andExpect(jsonPath("$.developer_message", is("Service with id [100] is not found")))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(status().is4xxClientError());
    }
}