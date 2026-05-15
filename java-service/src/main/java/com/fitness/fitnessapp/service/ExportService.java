package com.fitness.fitnessapp.service;

import com.fitness.fitnessapp.dto.export.ExportExercise;
import com.fitness.fitnessapp.dto.export.ExportFileResponse;
import com.fitness.fitnessapp.dto.export.ExportPlanDay;
import com.fitness.fitnessapp.dto.export.ExportPlanRequest;
import com.fitness.fitnessapp.entity.WorkoutPlan;
import com.fitness.fitnessapp.entity.WorkoutPlanItem;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Service
@RequiredArgsConstructor
public class ExportService {
    private static final Logger log = LoggerFactory.getLogger(ExportService.class);
    private final GoProgressClientService goProgressClientService;
    private final WorkoutPlanService workoutPlanService;

    public ResponseEntity<byte[]> downloadPlan(Long userId, Long planId, String format){
        log.info("Preparing plan data for export",
                kv("user_id", userId), kv("plan_id", planId), kv("format", format));

        WorkoutPlan plan = workoutPlanService.getUserPlan(userId, planId);
        Map<Integer, List<WorkoutPlanItem>> groupedByDay = plan.getItems().stream()
                .collect(Collectors.groupingBy(WorkoutPlanItem::getDayNumber));

        List<ExportPlanDay> days = new ArrayList<>();
        groupedByDay.forEach((dayNum, items) -> {
            List<ExportExercise> exercises = items.stream()
                    .map(i -> new ExportExercise(i.getExerciseName(), i.getSetsCount(), i.getRepsText()))
                    .collect(Collectors.toList());
            String focus = items.isEmpty() ? "Workout" : items.get(0).getFocus();
            days.add(new ExportPlanDay(dayNum, focus, exercises));
        });

        ExportPlanRequest exportRequest = new ExportPlanRequest();
        exportRequest.setUserId(userId);
        exportRequest.setPlanId(planId);
        exportRequest.setFormat(format);
        exportRequest.setPlanName(plan.getGoal());
        exportRequest.setDays(days);

        ResponseEntity<byte[]> goResponse = goProgressClientService.exportPlan(exportRequest);
        String extension = ExportFileResponse.getExtension(format);
        String fileName = "workout_plan_" + planId + extension;
        String contentType = "pdf".equalsIgnoreCase(format) ? "application/pdf" : "text/plain";

        ExportFileResponse fileResponse = new ExportFileResponse(fileName, contentType, goResponse.getBody());

        log.info("Plan export received from Go", kv("file_name", fileName));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileResponse.getFileName())
                .contentType(MediaType.parseMediaType(fileResponse.getContentType()))
                .body(fileResponse.getContent());
    }
}
