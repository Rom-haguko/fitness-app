package com.fitness.fitnessapp.controller;

import com.fitness.fitnessapp.dto.auth.RegisterRequest;
import com.fitness.fitnessapp.service.AuthService;
import com.fitness.fitnessapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    @GetMapping("/login")
    public String showLoginPage(){
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
                               BindingResult result, Model model){
        log.info("Registration attempt",kv("username",request.getUsername()));
        if (result.hasErrors()){
            return "auth/register";
        }

        if (!request.getPassword().equals(request.getConfirmPassword())){
            log.warn("Registration failed: password mismatch", kv("username", request.getUsername()));
            model.addAttribute("passwordError", "Passwords do not match");
            return "auth/register";
        }

        try{
            authService.register(request);
            log.info("Registration successful", kv("username", request.getUsername()));
        } catch (RuntimeException e) {
            log.error("Registration failed", kv("error", e.getMessage()));
            model.addAttribute("error",e.getMessage());
            return "auth/register";
        }

        return "redirect:/login?registered";
    }
}
