package com.nitaqat.nitaqat.controller;

import com.nitaqat.nitaqat.dto.ApiResponse;
import com.nitaqat.nitaqat.dto.ProfessionReportDTO;
import com.nitaqat.nitaqat.dto.ReportApiResponse;
import com.nitaqat.nitaqat.repository.ProfessionReportRepository;
import com.nitaqat.nitaqat.security.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProfessionReportController {

    private final ProfessionReportRepository reportRepository;
    @Autowired
    private JwtUtils jwtUtils;


    public ProfessionReportController(ProfessionReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @GetMapping("/api/profession-report")
    public ResponseEntity<ReportApiResponse<List<ProfessionReportDTO>>> getProfessionReport(
            @RequestParam(required = false) Long activityId,
            HttpServletRequest httpServletRequest
    ) {

        // ✅ Extract user ID from JWT
        String header = httpServletRequest.getHeader("Authorization");

        String token = header.substring(7);
        Long userId = jwtUtils.extractUserId(token);

        List<ProfessionReportDTO> report = reportRepository.getProfessionReport(activityId , userId);

        ReportApiResponse<List<ProfessionReportDTO>> response =
                new ReportApiResponse<>(true, "Professions report fetched successfully", HttpStatus.OK.value(), report);

        return ResponseEntity.ok(response);
    }
}
