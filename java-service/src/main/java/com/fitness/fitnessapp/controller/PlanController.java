package com.fitness.fitnessapp.controller;

import com.fitness.fitnessapp.entity.User;
import com.fitness.fitnessapp.entity.WorkoutPlan;
import com.fitness.fitnessapp.exception.NotFoundException;
import com.fitness.fitnessapp.service.UserService;
import com.fitness.fitnessapp.service.WorkoutPlanService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Controller
@RequiredArgsConstructor
@RequestMapping("/plans")
public class PlanController {
    private static final Logger log = LoggerFactory.getLogger(PlanController.class);
    private final WorkoutPlanService workoutPlanService;
    private final UserService userService;
    @GetMapping
    public String showPlanHistory(Model model, Principal principal){
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new NotFoundException("User not found"));
        log.debug("User viewing plan history", kv("user_id", user.getId()));
        List<WorkoutPlan> plans = workoutPlanService.getUserPlans(user.getId());
        model.addAttribute("plans",plans);
        return "plan/history";
    };

    @GetMapping("/{id}")
    public String showPlanDetails(@PathVariable("id") Long planId, Model model, Principal principal){
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new NotFoundException("User not found"));
        log.info("User viewing plan details", kv("plan_id", planId), kv("user_id", user.getId()));
        WorkoutPlan plan = workoutPlanService.getUserPlan(user.getId(), planId);
        model.addAttribute("plan", plan);
        return "plan/details";
    };

    @GetMapping("/generated/{id}")
    public String showPlanGenerated(@PathVariable("id") Long planId, Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new NotFoundException("User not found"));
        log.info("User viewing generated plan", kv("plan_id", planId), kv("user_id", user.getId()));
        WorkoutPlan plan = workoutPlanService.getUserPlan(user.getId(), planId);
        model.addAttribute("plan", plan);
        return "plan/generated";
    };
}
