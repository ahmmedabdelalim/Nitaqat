package com.nitaqat.nitaqat.controller;

import com.nitaqat.nitaqat.aspect.LogUserAction;
import com.nitaqat.nitaqat.dto.ActivitiesReportDTO;
import com.nitaqat.nitaqat.dto.ProfessionReportDTO;
import com.nitaqat.nitaqat.dto.ReportApiResponse;
import com.nitaqat.nitaqat.repository.ActivitiesReportRepository;
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
public class CalculationController {


    private final ActivitiesReportRepository activitiesReportRepository;

    private final ProfessionReportRepository reportRepository;
    @Autowired
    private JwtUtils jwtUtils;


    public CalculationController(ActivitiesReportRepository activitiesReportRepository, ProfessionReportRepository reportRepository) {
        this.activitiesReportRepository = activitiesReportRepository;
        this.reportRepository = reportRepository;
    }

    @LogUserAction(action = "Activity Calculation")
    @GetMapping("/api/activity-calculation")
    public ResponseEntity<ReportApiResponse<List<ActivitiesReportDTO>>> getActivitiesReport(
            @RequestParam(required = false) Long activityId
    )
    {
        List<ActivitiesReportDTO> report = activitiesReportRepository.getActivitiy(activityId);

        ReportApiResponse<List<ActivitiesReportDTO>> response =
                new ReportApiResponse<>(true, "Activity fetched successfully", HttpStatus.OK.value(), report);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/get-profession-for-activity")
    public ResponseEntity<ReportApiResponse<List<ProfessionReportDTO>>> getProfessionForActivity(
            @RequestParam(required = false) Long activityId,
            HttpServletRequest httpServletRequest
    ) {

        // ✅ Extract user ID from JWT
        String header = httpServletRequest.getHeader("Authorization");

        String token = header.substring(7);
        Long userId = jwtUtils.extractUserId(token);
        List<ProfessionReportDTO> report = reportRepository.getProfession(activityId , userId);

        ReportApiResponse<List<ProfessionReportDTO>> response =
                new ReportApiResponse<>(true, "Professions fetched successfully", HttpStatus.OK.value(), report);

        return ResponseEntity.ok(response);
    }

    @LogUserAction(action = "Profession Calculation")

    @GetMapping("/api/get-profession")
    public ResponseEntity<ReportApiResponse<List<ProfessionReportDTO>>> getProfession(
            @RequestParam(required = false) Long activityId,
            @RequestParam (required = false) Long professionId,
            HttpServletRequest httpServletRequest

    ) {
        // ✅ Extract user ID from JWT
        String header = httpServletRequest.getHeader("Authorization");

        String token = header.substring(7);
        Long userId = jwtUtils.extractUserId(token);
        List<ProfessionReportDTO> report = reportRepository.getProfession(activityId , userId);

        // If professionId is provided, filter in memory
        if (professionId != null) {
            report = report.stream()
                    .filter(r -> r.getId() == professionId.intValue())
                    .toList();
        }

        ReportApiResponse<List<ProfessionReportDTO>> response =
                new ReportApiResponse<>(true, "Profession fetched successfully", HttpStatus.OK.value(), report);

        return ResponseEntity.ok(response);
    }
}
