package com.fitness.fitnessapp.controller;

import com.fitness.fitnessapp.dto.auth.RegisterRequest;
import com.fitness.fitnessapp.service.AuthService;
import com.fitness.fitnessapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {
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
        if (result.hasErrors()){
            return "auth/register";
        }

        if (!request.getPassword().equals(request.getConfirmPassword())){
            model.addAttribute("passwordError", "Passwords do not match");
            return "auth/register";
        }

        try{
            authService.register(request);
        } catch (RuntimeException e) {
            model.addAttribute("error",e.getMessage());
            return "auth/register";
        }

        return "redirect:/login?registered";
    }
}
