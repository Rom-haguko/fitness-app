package com.fitness.fitnessapp.service;

import com.fitness.fitnessapp.dto.export.ExportPlanRequest;
import com.fitness.fitnessapp.dto.progress.ProgressSummaryResponse;
import com.fitness.fitnessapp.dto.progress.SaveWorkoutLogRequest;
import com.fitness.fitnessapp.exception.ExternalServiceException;
import com.fitness.fitnessapp.logging.LoggingUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Service
public class GoProgressClientService {
    private static final Logger log = LoggerFactory.getLogger(GoProgressClientService.class);
    private final WebClient goWebClient;

    public GoProgressClientService(WebClient goWebClient) {
        this.goWebClient = goWebClient;
    }

    public void saveWorkoutLog(SaveWorkoutLogRequest request){
        long startTime = System.currentTimeMillis();
        log.info("Executing saveWorkoutLog call to Go service",
                kv("user_id", request.getUserId()));
        try {
            goWebClient.post()
                    .uri("/api/v1/progress/logs")
                    .body(Mono.just(request), SaveWorkoutLogRequest.class)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            LoggingUtils.logExternalCallSuccess(log, "go-service", "/api/v1/progress/logs",
                    System.currentTimeMillis() - startTime, 200);
        } catch (Exception e){
            LoggingUtils.logExternalCallError(log, "go-service", "/api/v1/progress/logs",
                    System.currentTimeMillis() - startTime, e.getMessage());
            throw new ExternalServiceException("Go progress service is not responding", e);
        }
    };
    public ProgressSummaryResponse getProgressSummary(Long userId){
        long startTime = System.currentTimeMillis();
        log.info("Executing getProgressSummary call to Go service",
                kv("user_id", userId));
        try {
            ProgressSummaryResponse response = goWebClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/api/v1/statistics/summary")
                            .queryParam("user_id", userId).build())
                    .retrieve()
                    .bodyToMono(ProgressSummaryResponse.class)
                    .block();
            LoggingUtils.logExternalCallSuccess(log, "go-service", "/api/v1/statistics/summary",
                    System.currentTimeMillis() - startTime, 200);
            return response;
        } catch (Exception e) {
            LoggingUtils.logExternalCallError(log, "go-service", "/api/v1/statistics/summary",
                    System.currentTimeMillis() - startTime, e.getMessage());
            return new ProgressSummaryResponse();
        }
    };

    public ResponseEntity<byte[]> exportPlan(ExportPlanRequest request){
        long startTime = System.currentTimeMillis();
        log.info("Executing exportPlan call to Go service",
                kv("user_id", request.getUserId()), kv("plan_id", request.getPlanId()), kv("format", request.getFormat()));
        try {
            ResponseEntity<byte[]> response = goWebClient.post()
                    .uri("/api/v1/export/plan")
                    .body(Mono.just(request), ExportPlanRequest.class)
                    .retrieve()
                    .toEntity(byte[].class)
                    .block();
            LoggingUtils.logExternalCallSuccess(log, "go-service", "/export/plan",
                    System.currentTimeMillis() - startTime, 200);
            return response;
        } catch (Exception e) {
            LoggingUtils.logExternalCallError(log, "go-service", "/export/plan",
                    System.currentTimeMillis() - startTime, e.getMessage());
            throw new ExternalServiceException("Failed to export plan from Go service", e);
        }
    }

    public boolean healthCheck() {
        try {
            goWebClient.get().uri("/health").retrieve().toBodilessEntity().block();
            return true;
        } catch (Exception e) {
            log.warn("Go service health check failed", kv("error", e.getMessage()));
            return false;
        }
    }

    public void saveBodyWeight(long userId, double weight) {
        Map<String, Object> body = Map.of("user_id", userId, "weight", weight);
        goWebClient.post()
                .uri("/api/v1/body-weight")
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
};

