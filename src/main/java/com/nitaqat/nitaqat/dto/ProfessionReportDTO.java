package com.nitaqat.nitaqat.dto;

public record ProfessionReportDTO(
        int id,
        String companyCode,
        String companyName,
        String saudizationCatageory,
        String saudizationCatageoryAr,
        int empThreshold,
        int totalEmployees,
        int totalSaudiEmployees,
        Double requiredSaudizationPercentage,
        Double actualSaudizationPercentage


) {


    public int getId() {
        return id;
    }


}
