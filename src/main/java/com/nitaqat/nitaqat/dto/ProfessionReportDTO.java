package com.nitaqat.nitaqat.dto;

public record ProfessionReportDTO(
        String companyCode,
        String companyName,
        String saudizationCatageory,
        String saudizationCatageoryAr,
        int totalEmployees,
        int totalSaudiEmployees,
        Double requiredSaudizationPercentage,
        Double actualSaudizationPercentage
) {}
