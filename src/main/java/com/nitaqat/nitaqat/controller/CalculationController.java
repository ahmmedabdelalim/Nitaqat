package com.nitaqat.nitaqat.controller;

import com.nitaqat.nitaqat.dto.ActivitiesReportDTO;
import com.nitaqat.nitaqat.dto.ReportApiResponse;
import com.nitaqat.nitaqat.repository.ActivitiesReportRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CalculationController {


    private final ActivitiesReportRepository activitiesReportRepository;



    public CalculationController(ActivitiesReportRepository activitiesReportRepository) {
        this.activitiesReportRepository = activitiesReportRepository;
    }

    @GetMapping("/api/activity-calculation")
    public ResponseEntity<ReportApiResponse<List<ActivitiesReportDTO>>> getActivitiesReport(
            @RequestParam(required = false) Long activityId
    )
    {
        List<ActivitiesReportDTO> report = activitiesReportRepository.getActivitiesReport(activityId);

        ReportApiResponse<List<ActivitiesReportDTO>> response =
                new ReportApiResponse<>(true, "Activity fetched successfully", HttpStatus.OK.value(), report);

        return ResponseEntity.ok(response);
    }
}
