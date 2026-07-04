package com.nitaqat.nitaqat.service;

import com.nitaqat.nitaqat.dto.ActivitiesReportDTO;
import com.nitaqat.nitaqat.dto.ActivityWithSuggestionsDTO;
import com.nitaqat.nitaqat.dto.BandSuggestionDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ActivityBandSuggestionService {

    private record Band(String nameEn, String nameAr, double requiredPct) {}

    public ActivityWithSuggestionsDTO buildSuggestions(ActivitiesReportDTO dto) {
        double S = dto.totalSaudiEmployees();
        double T = dto.totalEmployees();
        double actual = dto.actualSaudizationPercentage() != null ? dto.actualSaudizationPercentage() : 0.0;

        List<Band> bands = List.of(
                new Band("Platinum", "بلاتيني", nz(dto.Platinum())),
                new Band("High Green", "أخضر مرتفع", nz(dto.HighGreen())),
                new Band("Medium Green", "أخضر متوسط", nz(dto.MiddelGreen())),
                new Band("Low Green", "أخضر منخفض", nz(dto.LowGreen())),
                new Band("Red", "أحمر", 0.0)
        );

        int currentIndex = determineCurrentBandIndex(actual, bands);
        Band currentBand = bands.get(currentIndex);

        List<BandSuggestionDTO> suggestions = new ArrayList<>();

        for (int i = 0; i < bands.size(); i++) {
            if (i == currentIndex) continue;

            Band band = bands.get(i);

            if (i < currentIndex) {
                // Band ABOVE current -> hire Saudis
                double targetPct = band.requiredPct();
                Integer regular = hiresNeededRegular(S, T, targetPct);
                Integer disabled = hiresNeededDisabled(S, T, targetPct);

                suggestions.add(new BandSuggestionDTO(
                        band.nameEn(), band.nameAr(),
                        "hireSaudi",
                        regular == null
                                ? "Cannot reach " + band.nameEn() + " by hiring alone"
                                : "Hire " + regular + " more regular Saudi employee(s) to reach " + band.nameEn(),
                        regular == null
                                ? "لا يمكن الوصول إلى " + band.nameAr() + " بالتوظيف فقط"
                                : "يلزم تعيين " + regular + " سعودي إضافي للوصول إلى " + band.nameAr()
                ));
                suggestions.add(new BandSuggestionDTO(
                        band.nameEn(), band.nameAr(),
                        "hireDisabledSaudi",
                        disabled == null
                                ? "Cannot reach " + band.nameEn() + " by hiring alone"
                                : "Or hire " + disabled + " disabled Saudi employee(s) to reach " + band.nameEn(),
                        disabled == null
                                ? "لا يمكن الوصول إلى " + band.nameAr() + " بالتوظيف فقط"
                                : "أو تعيين " + disabled + " سعودي ذوي إعاقة للوصول إلى " + band.nameAr()
                ));
            } else {
                // Band BELOW current -> hire foreigners
                double upperBoundToBreak = currentBand.requiredPct();
                double lowerBoundOfTarget = band.requiredPct();

                Integer minForeigners = foreignersNeededToDropBelow(S, T, upperBoundToBreak);
                Integer maxForeigners = band.nameEn().equals("Red")
                        ? null
                        : foreignersNeededToDropBelow(S, T, lowerBoundOfTarget);

                Integer maxInBand = (maxForeigners == null) ? null : Math.max(minForeigners == null ? 0 : minForeigners, maxForeigners - 1);

                suggestions.add(new BandSuggestionDTO(
                        band.nameEn(), band.nameAr(),
                        "hireForeigner",
                        buildForeignerTextEn(minForeigners, maxInBand, band.nameEn()),
                        buildForeignerTextAr(minForeigners, maxInBand, band.nameAr())
                ));
            }
        }

        return new ActivityWithSuggestionsDTO(currentBand.nameEn(), currentBand.nameAr(), suggestions);

    }

    private int determineCurrentBandIndex(double actual, List<Band> bands) {
        if (actual >= bands.get(0).requiredPct()) return 0;
        if (actual >= bands.get(1).requiredPct()) return 1;
        if (actual >= bands.get(2).requiredPct()) return 2;
        if (actual >= bands.get(3).requiredPct()) return 3;
        return 4;
    }

    private double nz(Double val) {
        return val != null ? val : 0.0;
    }

    private Integer hiresNeededRegular(double S, double T, double R) {
        if (R >= 100) return null;
        double raw = (R * T - 100.0 * S) / (100.0 - R);
        return (int) Math.max(0, Math.ceil(raw));
    }

    private Integer hiresNeededDisabled(double S, double T, double R) {
        if (R >= 100) return null;
        double raw = (R * T - 100.0 * S) / (4.0 * (100.0 - R));
        return (int) Math.max(0, Math.ceil(raw));
    }

    private Integer foreignersNeededToDropBelow(double S, double T, double boundary) {
        if (boundary <= 0) return null;
        double raw = (100.0 * S / boundary) - T;
        return (int) Math.max(0, Math.floor(raw) + 1);
    }

    private String buildForeignerTextEn(Integer min, Integer max, String bandNameEn) {
        if (min == null) return "Cannot compute — invalid band boundary";
        if (max == null) return "Hire " + min + "+ more foreign employee(s) to drop into " + bandNameEn;
        if (max.equals(min)) return "Hire " + min + " more foreign employee(s) to drop into " + bandNameEn;
        return "Hire between " + min + " and " + max + " more foreign employee(s) to land in " + bandNameEn;
    }

    private String buildForeignerTextAr(Integer min, Integer max, String bandNameAr) {
        if (min == null) return "لا يمكن الحساب — حد النطاق غير صالح";
        if (max == null) return "يلزم تعيين " + min + " أجنبي أو أكثر للوصول إلى " + bandNameAr;
        if (max.equals(min)) return "يلزم تعيين " + min + " أجنبي للوصول إلى " + bandNameAr;
        return "يلزم تعيين بين " + min + " و " + max + " أجنبي للوصول إلى " + bandNameAr;
    }
}