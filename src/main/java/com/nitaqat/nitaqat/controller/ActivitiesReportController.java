package com.nitaqat.nitaqat.controller;


import com.nitaqat.nitaqat.aspect.LogUserAction;
import com.nitaqat.nitaqat.dto.ActivitiesAnalysisDTO;
import com.nitaqat.nitaqat.dto.ActivitiesReportDTO;
import com.nitaqat.nitaqat.dto.ProfessionReportDTO;
import com.nitaqat.nitaqat.dto.ReportApiResponse;
import com.nitaqat.nitaqat.repository.ActivitiesReportRepository;
import com.nitaqat.nitaqat.security.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.EntityResponse;

import java.util.List;

@RestController
public class ActivitiesReportController {

    private final ActivitiesReportRepository activitiesReportRepository;
    @Autowired
    private JwtUtils jwtUtils;

    public ActivitiesReportController(ActivitiesReportRepository activitiesReportRepository) {
        this.activitiesReportRepository = activitiesReportRepository;
    }

    @LogUserAction(action = "Activity Report")
    @GetMapping("/api/activity-report")
    public ResponseEntity<ReportApiResponse<List<ActivitiesReportDTO>>> getActivitiesReport(
            @RequestParam(required = false) Long activityId , HttpServletRequest httpServletRequest
    )
    {
        // ✅ Extract user ID from JWT
        String header = httpServletRequest.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ReportApiResponse<>(false, "Missing or invalid token", 401, null));
        }

        String token = header.substring(7);
        Long userId = jwtUtils.extractUserId(token);
    /// ///////////

        List<ActivitiesReportDTO> report = activitiesReportRepository.getActivitiesReport(activityId , userId);

        ReportApiResponse<List<ActivitiesReportDTO>> response =
                new ReportApiResponse<>(true, "Activity report fetched successfully", HttpStatus.OK.value(), report);

        return ResponseEntity.ok(response);
    }

    @LogUserAction(action = "Activity Nationalities Report")
    @GetMapping("/api/get-activity-analysis")
    public ResponseEntity<ReportApiResponse<List<ActivitiesAnalysisDTO>>> getActivitieAnalysis(
            @RequestParam(required = false) Long activityId
    )
    {
        List<ActivitiesAnalysisDTO> report = activitiesReportRepository.getActivitieAnalysis(activityId);

        ReportApiResponse<List<ActivitiesAnalysisDTO>> response =
                new ReportApiResponse<>(true, "Activity Analysis fetched successfully", HttpStatus.OK.value(), report);

        return ResponseEntity.ok(response);
    }
}
