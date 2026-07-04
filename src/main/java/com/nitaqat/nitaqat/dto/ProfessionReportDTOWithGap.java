package com.nitaqat.nitaqat.dto;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

public record ProfessionReportDTOWithGap(
        int id,
        String companyCode,
        String companyName,
        String saudizationCategory,
        String saudizationCategoryAr,
        int empThreshold,
        int totalEmployees,
        int totalSaudiEmployees,
        double requiredSaudizationPercentage,
        double actualSaudizationPercentage,
        Integer gapReplaceExisting,
        String gapReplaceExistingTextEn,
        String gapReplaceExistingTextAr,
        Integer gapNewHires,
        String gapNewHiresTextEn,
        String gapNewHiresTextAr,
        Integer neededForThreshold,
        String neededForThresholdTextEn,
        String neededForThresholdTextAr

) {
    public int getId() {
        return id;
    }
}
