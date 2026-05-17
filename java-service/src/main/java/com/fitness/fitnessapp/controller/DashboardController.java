package com.fitness.fitnessapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Controller
public class DashboardController {
    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);
    @GetMapping("/dashboard")
    public String showDashboardPage(@AuthenticationPrincipal UserDetails userDetails, Model model){
        String username = userDetails.getUsername();
        log.info("User accessed dashboard", kv("username", username));
        model.addAttribute("username", username);
        return "dashboard";
    }
}
