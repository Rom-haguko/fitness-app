package com.fitness.fitnessapp.service;

import com.fitness.fitnessapp.dto.plan.GeneratePlanRequest;
import com.fitness.fitnessapp.dto.plan.GeneratePlanResponse;
import com.fitness.fitnessapp.exception.ExternalServiceException;
import com.fitness.fitnessapp.logging.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Service
public class PythonPlanClientService {
    private static final Logger log = LoggerFactory.getLogger(PythonPlanClientService.class);
    private final WebClient pythonWebClient;

    public PythonPlanClientService(@Qualifier("pythonWebClient") WebClient pythonWebClient) {
        this.pythonWebClient = pythonWebClient;
    }

    public GeneratePlanResponse generatePlan(GeneratePlanRequest request){
        long startTime = System.currentTimeMillis();
        try {
            GeneratePlanResponse response = pythonWebClient.post()
                    .uri("/api/v1/workout_plans/generate")
                    .body(Mono.just(request), GeneratePlanRequest.class)
                    .retrieve()
                    .bodyToMono(GeneratePlanResponse.class)
                    .block();
            LoggingUtils.logExternalCallSuccess(log, "python-service", "generate",
                    System.currentTimeMillis() - startTime, 200);
            return response;
        } catch (Exception e) {
            LoggingUtils.logExternalCallError(log, "python-service", "generate",
                    System.currentTimeMillis() - startTime, e.getMessage());
            throw new ExternalServiceException("AI Service communication failed", e);
        }
    };
    public boolean healthCheck() {
        try {
            pythonWebClient.get()
                    .uri("/")
                    .retrieve()
                    .toBodilessEntity()
                    .block(java.time.Duration.ofSeconds(10));
            return true;
        } catch (Exception e) {
            log.warn("Health check failed for Python service", kv("error", e.getMessage()));
            return false;
        }
    }
}
