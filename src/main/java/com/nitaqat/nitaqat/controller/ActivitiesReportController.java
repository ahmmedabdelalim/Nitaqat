package com.nitaqat.nitaqat.controller;


import com.nitaqat.nitaqat.dto.ActivitiesReportDTO;
import com.nitaqat.nitaqat.dto.ProfessionReportDTO;
import com.nitaqat.nitaqat.dto.ReportApiResponse;
import com.nitaqat.nitaqat.repository.ActivitiesReportRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.EntityResponse;

import java.util.List;

@RestController
public class ActivitiesReportController {

    private final ActivitiesReportRepository activitiesReportRepository;

    public ActivitiesReportController(ActivitiesReportRepository activitiesReportRepository) {
        this.activitiesReportRepository = activitiesReportRepository;
    }

    @GetMapping("/api/activity-report")
    public ResponseEntity<ReportApiResponse<List<ActivitiesReportDTO>>> getActivitiesReport()
    {
        List<ActivitiesReportDTO> report = activitiesReportRepository.getActivitiesReport();

        ReportApiResponse<List<ActivitiesReportDTO>> response =
                new ReportApiResponse<>(true, "Profession report fetched successfully", HttpStatus.OK.value(), report);

        return ResponseEntity.ok(response);
    }
}
