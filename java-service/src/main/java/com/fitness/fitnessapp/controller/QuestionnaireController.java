package com.fitness.fitnessapp.controller;

import com.fitness.fitnessapp.dto.plan.GeneratePlanResponse;
import com.fitness.fitnessapp.dto.questionnaire.QuestionnaireForm;
import com.fitness.fitnessapp.entity.User;
import com.fitness.fitnessapp.entity.WorkoutPlan;
import com.fitness.fitnessapp.exception.NotFoundException;
import com.fitness.fitnessapp.logging.LoggingUtils;
import com.fitness.fitnessapp.service.UserService;
import com.fitness.fitnessapp.service.WorkoutPlanService;
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
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Controller
@RequiredArgsConstructor
@RequestMapping("/questionnaire")
public class QuestionnaireController {
    private static final Logger log = LoggerFactory.getLogger(QuestionnaireController.class);
    private final WorkoutPlanService workoutPlanService;
    private final UserService userService;

    @GetMapping
    public String showQuestionnaireForm(Model model){
        model.addAttribute("questionnaireForm", new QuestionnaireForm());
        return "questionnaire/form";
    };

    @PostMapping
    public String submitQuestionnaire(@Valid @ModelAttribute("questionnaireForm")QuestionnaireForm form, BindingResult result, Principal principal){
        if (result.hasErrors()) {
            log.warn("Form validation failed", kv("errors_count", result.getErrorCount()));
            return "questionnaire/form";
        }
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new NotFoundException("Current user not found"));
        log.info("User is generating a new plan", kv("username", user.getUsername()));
        GeneratePlanResponse response = workoutPlanService.generatePlan(form);
        WorkoutPlan savedPlan = workoutPlanService.saveGeneratedPlan(user.getId(),form,response);
        LoggingUtils.logUserAction(log, "PLAN_GENERATION_SUCCESS", user.getId(), user.getUsername());

        return "redirect:/plans/" + savedPlan.getId();
    };
}
