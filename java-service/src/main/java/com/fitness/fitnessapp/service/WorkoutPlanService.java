package com.fitness.fitnessapp.service;

import com.fitness.fitnessapp.dto.plan.GeneratePlanRequest;
import com.fitness.fitnessapp.dto.plan.GeneratePlanResponse;
import com.fitness.fitnessapp.dto.questionnaire.QuestionnaireForm;
import com.fitness.fitnessapp.entity.User;
import com.fitness.fitnessapp.entity.WorkoutPlan;
import com.fitness.fitnessapp.exception.ExternalServiceException;
import com.fitness.fitnessapp.exception.NotFoundException;
import com.fitness.fitnessapp.logging.LoggingUtils;
import com.fitness.fitnessapp.mapper.WorkoutPlanMapper;
import com.fitness.fitnessapp.repository.UserRepository;
import com.fitness.fitnessapp.repository.WorkoutPlanRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Service
@RequiredArgsConstructor
public class WorkoutPlanService {
    private static final Logger log = LoggerFactory.getLogger(WorkoutPlanService.class);
    private final UserRepository userRepository;
    private final WorkoutPlanRepository workoutPlanRepository;
    private final PythonPlanClientService pythonPlanClientService;
    private final WorkoutPlanMapper workoutPlanMapper;

    public GeneratePlanResponse generatePlan(User user, QuestionnaireForm form){
        boolean isAvailable = pythonPlanClientService.healthCheck();
        log.debug("Python service health check", kv("is_available", isAvailable));

        if (!isAvailable) {
            log.error("Plan generation aborted: Python service offline");
            throw new ExternalServiceException("Python AI service is not responding");
        }
        log.info("Preparing data for Python AI service", kv("goal", form.getGoal()));
        GeneratePlanRequest request = new GeneratePlanRequest();
        request.setUserId(user.getId());
        request.setGoal(form.getGoal());
        request.setLevel(form.getLevel());
        request.setDaysPerWeek(form.getDaysPerWeek());
        request.setWeight(form.getWeight());
        request.setHeight(form.getHeight());
        request.setSplitType(form.getSplitType());
        request.setRestrictions(form.getRestrictions());

        return pythonPlanClientService.generatePlan(request);

    };

    @Transactional
    public WorkoutPlan saveGeneratedPlan(Long userId, QuestionnaireForm form, GeneratePlanResponse response){
        log.info("Persisting generated plan to database", kv("user_id", userId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        WorkoutPlan plan = workoutPlanMapper.mapResponseToEntity(response);
        plan.setUser(user);
        plan.setGoal(form.getGoal());
        plan.setLevel(form.getLevel());
        plan.setDaysPerWeek(form.getDaysPerWeek());
        plan.setSplitType(form.getSplitType());

        WorkoutPlan savedPlan = workoutPlanRepository.save(plan);
        LoggingUtils.logEntityCreated(log, "WorkoutPlan", savedPlan.getId());
        return savedPlan;
    };

    @Transactional(readOnly = true)
    public List<WorkoutPlan> getUserPlans(Long userId){
        log.debug("Fetching plan history", kv("user_id", userId));
        return workoutPlanRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    };

    @Transactional(readOnly = true)
    public WorkoutPlan getUserPlan(Long userId, Long planId){
        log.debug("Fetching plan details", kv("user_id", userId), kv("plan_id", planId));
        WorkoutPlan plan = workoutPlanRepository.findByIdAndUserId(planId,userId)
                .orElseThrow(() -> new NotFoundException("Workout plan not found"));
        if (plan.getItems() != null) {
            plan.getItems().size();
        }
        return plan;
    };
}
