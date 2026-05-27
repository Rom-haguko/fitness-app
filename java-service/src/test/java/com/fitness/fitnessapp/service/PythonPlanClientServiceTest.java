package com.fitness.fitnessapp.service;

import com.fitness.fitnessapp.dto.plan.GeneratePlanRequest;
import com.fitness.fitnessapp.dto.plan.GeneratePlanResponse;
import com.fitness.fitnessapp.dto.plan.PlanDayDto;
import com.fitness.fitnessapp.exception.ExternalServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PythonPlanClientServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private PythonPlanClientService pythonPlanClientService;

    @Test
    @DisplayName("generatePlan should return response when API is successful")
    void generatePlan_Success() {
        // Given
        GeneratePlanRequest request = new GeneratePlanRequest();
        GeneratePlanResponse expectedResponse = new GeneratePlanResponse();
        expectedResponse.setWorkoutPlan(new ArrayList<PlanDayDto>());

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(), any(Class.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(GeneratePlanResponse.class)).thenReturn(Mono.just(expectedResponse));

        // When
        GeneratePlanResponse actualResponse = pythonPlanClientService.generatePlan(request);

        // Then
        assertNotNull(actualResponse);
        verify(webClient).post();
    }

    @Test
    @DisplayName("generatePlan should throw ExternalServiceException on network error")
    void generatePlan_NetworkError() {
        // Given
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(), any(Class.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        
        when(responseSpec.bodyToMono(GeneratePlanResponse.class)).thenReturn(Mono.error(new RuntimeException("Network error")));

        // When & Then
        assertThrows(ExternalServiceException.class, () -> pythonPlanClientService.generatePlan(new GeneratePlanRequest()));
    }
}