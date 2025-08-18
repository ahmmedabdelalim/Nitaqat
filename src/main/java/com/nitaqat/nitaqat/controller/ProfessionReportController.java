package com.nitaqat.nitaqat.controller;

import com.nitaqat.nitaqat.dto.ProfessionReportDTO;
import com.nitaqat.nitaqat.repository.ProfessionReportRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProfessionReportController {

    private final ProfessionReportRepository reportRepository;

    public ProfessionReportController(ProfessionReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @GetMapping("/api/profession-report")
    public List<ProfessionReportDTO> getProfessionReport() {
        return reportRepository.getProfessionReport();
    }
}
