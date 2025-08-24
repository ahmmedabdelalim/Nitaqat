package com.nitaqat.nitaqat.controller;

import com.nitaqat.nitaqat.dto.ProfessionReportDTO;
import com.nitaqat.nitaqat.dto.ReportApiResponse;
import com.nitaqat.nitaqat.repository.ProfessionReportRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProfessionReportController {

    private final ProfessionReportRepository reportRepository;

    public ProfessionReportController(ProfessionReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @GetMapping("/api/profession-report")
    public ResponseEntity<ReportApiResponse<List<ProfessionReportDTO>>> getProfessionReport(
            @RequestParam(required = false) Long activityId
    ) {

        List<ProfessionReportDTO> report = reportRepository.getProfessionReport(activityId);

        ReportApiResponse<List<ProfessionReportDTO>> response =
                new ReportApiResponse<>(true, "Profession report fetched successfully", HttpStatus.OK.value(), report);

        return ResponseEntity.ok(response);
    }
}
