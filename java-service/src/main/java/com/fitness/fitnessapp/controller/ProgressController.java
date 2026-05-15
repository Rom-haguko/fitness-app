package com.fitness.fitnessapp.controller;

import com.fitness.fitnessapp.dto.progress.ProgressSummaryResponse;
import com.fitness.fitnessapp.dto.progress.SaveWorkoutLogRequest;
import com.fitness.fitnessapp.dto.progress.WorkoutLogForm;
import com.fitness.fitnessapp.entity.User;
import com.fitness.fitnessapp.exception.NotFoundException;
import com.fitness.fitnessapp.service.GoProgressClientService;
import com.fitness.fitnessapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Controller
@RequiredArgsConstructor
@RequestMapping("/progress")
public class ProgressController {
    private static final Logger log = LoggerFactory.getLogger(ProgressController.class);
    private final GoProgressClientService goProgressClientService;
    private final UserService userService;
    @GetMapping
    public String showProgressSummary(Model model, Principal principal){
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new NotFoundException("User not found"));
        log.debug("User viewing progress summary", kv("user_id", user.getId()));
        ProgressSummaryResponse summary = goProgressClientService.getProgressSummary(user.getId());
        model.addAttribute("summary", summary);
        return "progress/summary";
    };
    @GetMapping("/log")
    public String showWorkoutLogForm(@RequestParam Long workoutPlanId, @RequestParam Long exerciseId, Model model){
        WorkoutLogForm form = new WorkoutLogForm();
        form.setWorkoutPlanId(workoutPlanId);
        form.setExerciseId(exerciseId);
        model.addAttribute("workoutLogForm", form);
        return "progress/log-workout";
    };
    @PostMapping("/log")
    public String submitWorkoutLog(@Valid @ModelAttribute("workoutLogForm") WorkoutLogForm form, BindingResult result, Principal principal){
        if (result.hasErrors()) {
            log.warn("Form validation failed", kv("errors_count", result.getErrorCount()));
            return "progress/log-workout";
        }
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new NotFoundException("User not found"));
        log.info("User is recording workout logs", kv("username", user.getUsername()));
        SaveWorkoutLogRequest request = new SaveWorkoutLogRequest(
                user.getId(), form.getWorkoutPlanId(), form.getExerciseId(),
                form.getSets(), form.getReps(), form.getWeight()
        );
        goProgressClientService.saveWorkoutLog(request);
        return "redirect:/progress?success=true";
    };
}
