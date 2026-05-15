package com.fitness.fitnessapp.controller;

import com.fitness.fitnessapp.entity.User;
import com.fitness.fitnessapp.exception.NotFoundException;
import com.fitness.fitnessapp.service.ExportService;
import com.fitness.fitnessapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Controller
@RequiredArgsConstructor
@RequestMapping("/export")
public class ExportController {
    private static final Logger log = LoggerFactory.getLogger(ExportController.class);
    private final ExportService exportService;
    private final UserService userService;

    @GetMapping("/plan/{planId}")
    public ResponseEntity<byte[]> downloadPlan(@PathVariable("planId") Long planId,
                                               @RequestParam("format") String format,
                                               Principal principal) {

        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new NotFoundException("User not found"));

        log.info("User requested plan download",
                kv("username", user.getUsername()), kv("plan_id", planId), kv("format", format));

        return exportService.downloadPlan(user.getId(), planId, format);
    }
}

