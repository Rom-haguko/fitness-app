package com.fitness.fitnessapp.service;

import com.fitness.fitnessapp.dto.progress.ProgressSummaryResponse;
import com.fitness.fitnessapp.dto.progress.SaveWorkoutLogRequest;
import com.fitness.fitnessapp.exception.ExternalServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoProgressClientServiceTest {

    @Mock private WebClient webClient;
    @Mock private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock private WebClient.ResponseSpec responseSpec;
    @Mock private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    private GoProgressClientService goProgressClientService;

    @BeforeEach
    void setUp() {
        goProgressClientService = new GoProgressClientService(webClient);
    }

    @Test
    @DisplayName("saveWorkoutLog should successfully call Go service")
    void saveWorkoutLog_Success() {
        SaveWorkoutLogRequest request = new SaveWorkoutLogRequest();

        when(webClient.post()).thenReturn((WebClient.RequestBodyUriSpec) requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(), any(Class.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

        assertDoesNotThrow(() -> goProgressClientService.saveWorkoutLog(request));
        verify(webClient).post();
    }

    @Test
    @DisplayName("getProgressSummary should return summary from Go service")
    void getProgressSummary_Success() {
        ProgressSummaryResponse expected = new ProgressSummaryResponse();
        expected.setTotalWorkouts(5);

        when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ProgressSummaryResponse.class)).thenReturn(Mono.just(expected));

        ProgressSummaryResponse result = goProgressClientService.getProgressSummary(1L);

        assertNotNull(result);
        assertEquals(5, result.getTotalWorkouts());
    }

    @Test
    @DisplayName("saveBodyWeight should call Go service")
    void saveBodyWeight_Success() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

        assertDoesNotThrow(() -> goProgressClientService.saveBodyWeight(1L, 75.0));
        verify(webClient).post();
    }

    @Test
    @DisplayName("healthCheck should return false when Go is down")
    void healthCheck_Failure() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/health")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.error(new RuntimeException("Down")));

        boolean isHealthy = goProgressClientService.healthCheck();

        assertFalse(isHealthy);
    }
}