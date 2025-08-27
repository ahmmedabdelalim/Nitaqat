package com.nitaqat.nitaqat.controller;


import com.nitaqat.nitaqat.dto.ActivityHierarchyDTO;
import com.nitaqat.nitaqat.dto.ReportApiResponse;
import com.nitaqat.nitaqat.entity.Activity;
import com.nitaqat.nitaqat.repository.ActivityRepository;
import com.nitaqat.nitaqat.service.ActivityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ActivityController {

    private final ActivityRepository activityRepository;

    private final ActivityService activityService;

    public ActivityController(ActivityRepository activityRepository, ActivityService activityService) {
        this.activityRepository = activityRepository;
        this.activityService = activityService;
    }

    @GetMapping("api/activities")
    public ResponseEntity<ReportApiResponse<List<Activity>>> getAllActivities() {
        List<Activity> activities = activityRepository.findAll();

        ReportApiResponse<List<Activity>> response =
                new ReportApiResponse<>(
                        true,
                        "Activities fetched successfully",
                        HttpStatus.OK.value(),
                        activities
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("api/activites/hierarchy")
    public ResponseEntity<ReportApiResponse<List<ActivityHierarchyDTO>>> getHierarchy() {
        List<ActivityHierarchyDTO> hierarchy = activityService.getHierarchy();

        ReportApiResponse<List<ActivityHierarchyDTO>> response =
                new ReportApiResponse<>(
                        true,
                        "Activities hierarchy fetched successfully",
                        HttpStatus.OK.value(),
                        hierarchy
                );

        return ResponseEntity.ok(response);
    }





}
