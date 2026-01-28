package com.nitaqat.nitaqat.dto;


public record ActivitiesReportDTO(
        int Id,
        String companyCode,
        String companyName,
        int totalEmployees,
        int totalSaudiEmployees,
        Double requiredSaudizationPercentage,
        Double actualSaudizationPercentage,
        Double LowGreen,
        Double MiddelGreen,
        Double HighGreen,
        Double Platinum
) {}