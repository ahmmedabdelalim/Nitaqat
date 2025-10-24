package com.nitaqat.nitaqat.controller;


import com.nitaqat.nitaqat.dto.ActivityDto;
import com.nitaqat.nitaqat.dto.ActivityHierarchyDTO;
import com.nitaqat.nitaqat.dto.ApiResponse;
import com.nitaqat.nitaqat.dto.ReportApiResponse;
import com.nitaqat.nitaqat.entity.Activity;
import com.nitaqat.nitaqat.repository.ActivityRepository;
import com.nitaqat.nitaqat.security.JwtUtils;
import com.nitaqat.nitaqat.service.ActivityService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ActivityController {

    private final ActivityRepository activityRepository;
    @Autowired
    private JwtUtils jwtUtils;
    private final ActivityService activityService;

    public ActivityController(ActivityRepository activityRepository, ActivityService activityService) {
        this.activityRepository = activityRepository;
        this.activityService = activityService;
    }

    @GetMapping("api/activities")
    public ResponseEntity<ReportApiResponse<List<ActivityDto>>> getAllActivities(HttpServletRequest httpServletRequest) {
        // ✅ Extract user ID from JWT
        String header = httpServletRequest.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ReportApiResponse<>(false, "Missing or invalid token", 401, null));
        }

        String token = header.substring(7);
        Long userId = jwtUtils.extractUserId(token);

        // ✅ Fetch activities belonging to the user
        List<ActivityDto> activities = activityRepository.findAllIdAndNameByUserId(userId);

        ReportApiResponse<List<ActivityDto>> response = new ReportApiResponse<>(
                true,
                "Activities fetched successfully",
                HttpStatus.OK.value(),
                activities
        );

        return ResponseEntity.ok(response);
    }




}
