package com.fitness.fitnessapp.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static net.logstash.logback.argument.StructuredArguments.kv;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception e, Model model) {
        log.error("Internal server error", kv("error", e.getMessage()));
        model.addAttribute("errorStatus", "500 - Internal Server Error");
        model.addAttribute("errorMessage", "Something went wrong. Please try again later.");
        return "error";
    }

    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException e, Model model) {
        log.warn("Business logic exception", kv("message", e.getMessage()));
        model.addAttribute("errorStatus", "400 - Error");
        model.addAttribute("errorMessage", e.getMessage());
        return "error";
    }

    @ExceptionHandler(NotFoundException.class)
    public String handleNotFoundException(NotFoundException e, Model model) {
        log.warn("Resource not found", kv("message", e.getMessage()));
        model.addAttribute("errorStatus", "404 - Not Found");
        model.addAttribute("errorMessage", e.getMessage());
        return "error";
    }

    @ExceptionHandler(ValidationException.class)
    public String handleValidationException(ValidationException e, Model model) {
        log.warn("Validation Error", kv("message", e.getMessage()));
        model.addAttribute("errorStatus", "400 - Bad Request");
        model.addAttribute("errorMessage", e.getMessage());
        return "error";
    }

    @ExceptionHandler(ExternalServiceException.class)
    public String handleExternalServiceException(ExternalServiceException e, Model model) {
        log.error("External Server Error", kv("error", e.getMessage()));
        model.addAttribute("errorStatus", "502 - Bad Gateway");
        model.addAttribute("errorMessage", "External service is temporarily unavailable. Please try again later.");
        return "error";
    }

    @ExceptionHandler(DataAccessException.class)
    public String handleDatabaseException(DataAccessException e, Model model) {
        log.error("Database error", kv("error", e.getMessage()));
        model.addAttribute("errorStatus", "500 - Database Error");
        model.addAttribute("errorMessage", "An error occurred while accessing the database.");
        return "error";
    }
}