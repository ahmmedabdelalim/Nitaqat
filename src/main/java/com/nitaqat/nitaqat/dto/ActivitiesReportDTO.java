package com.nitaqat.nitaqat.dto;


public record ActivitiesReportDTO(
        String companyCode,
        String companyName,
        int totalEmployees,
        int totalSaudiEmployees,
        Double requiredSaudizationPercentage,
        Double actualSaudizationPercentage
) {}