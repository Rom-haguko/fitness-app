package com.fitness.fitnessapp.controller;

import com.fitness.fitnessapp.entity.User;
import com.fitness.fitnessapp.entity.WorkoutPlan;
import com.fitness.fitnessapp.exception.NotFoundException;
import com.fitness.fitnessapp.service.UserService;
import com.fitness.fitnessapp.service.WorkoutPlanService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Controller
@RequiredArgsConstructor
public class DashboardController {
    private final UserService userService;
    private final WorkoutPlanService workoutPlanService;
    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);
    @GetMapping("/dashboard")
    public String showDashboardPage(@AuthenticationPrincipal UserDetails userDetails, Model model){
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));
        log.info("User accessed dashboard", kv("username", user.getUsername()));
        model.addAttribute("username", user.getUsername());
        List<WorkoutPlan> history = workoutPlanService.getUserPlans(user.getId());

        if (!history.isEmpty()) {
            model.addAttribute("latestPlanId", history.get(0).getId());
            model.addAttribute("latestPlanName", history.get(0).getGoal());
        }
        return "dashboard";
    }
}
