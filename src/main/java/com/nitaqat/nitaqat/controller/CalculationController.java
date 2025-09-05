package com.nitaqat.nitaqat.controller;

import com.nitaqat.nitaqat.dto.ActivitiesReportDTO;
import com.nitaqat.nitaqat.dto.ProfessionReportDTO;
import com.nitaqat.nitaqat.dto.ReportApiResponse;
import com.nitaqat.nitaqat.repository.ActivitiesReportRepository;
import com.nitaqat.nitaqat.repository.ProfessionReportRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CalculationController {


    private final ActivitiesReportRepository activitiesReportRepository;

    private final ProfessionReportRepository reportRepository;


    public CalculationController(ActivitiesReportRepository activitiesReportRepository, ProfessionReportRepository reportRepository) {
        this.activitiesReportRepository = activitiesReportRepository;
        this.reportRepository = reportRepository;
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

    @GetMapping("/api/professions-calculation")
    public ResponseEntity<ReportApiResponse<List<ProfessionReportDTO>>> getProfessionReport(
            @RequestParam(required = false) Long activityId,
            @RequestParam (required = false) Long professionId
    ) {

        List<ProfessionReportDTO> report = reportRepository.getProfession(activityId , professionId);

        ReportApiResponse<List<ProfessionReportDTO>> response =
                new ReportApiResponse<>(true, "Professions report fetched successfully", HttpStatus.OK.value(), report);

        return ResponseEntity.ok(response);
    }
}
