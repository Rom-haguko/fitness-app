package com.fitness.fitnessapp.logging;

import org.slf4j.Logger;
import static net.logstash.logback.argument.StructuredArguments.kv;

public final class LoggingUtils {

    private LoggingUtils() {
    }

    public static void logUserAction(Logger log, String action, Long userId, String username) {
        log.info("User action executed",
                kv("action", action),
                kv("user_id", userId),
                kv("username", username));
    }

    public static void logExternalCallSuccess(Logger log, String serviceName, String endpoint, long durationMs, int statusCode) {
        log.info("External service call completed",
                kv("service", serviceName),
                kv("endpoint", endpoint),
                kv("duration_ms", durationMs),
                kv("status", statusCode));
    }

    public static void logExternalCallError(Logger log, String serviceName, String endpoint, long durationMs, String errorMessage) {
        log.error("External service call failed",
                kv("service", serviceName),
                kv("endpoint", endpoint),
                kv("duration_ms", durationMs),
                kv("error", errorMessage));
    }

    public static void logEntityCreated(Logger log, String entityName, Long entityId) {
        log.info("Entity created successfully",
                kv("entity", entityName),
                kv("entity_id", entityId));
    }
}