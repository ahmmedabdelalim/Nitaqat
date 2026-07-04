package com.nitaqat.nitaqat.dto;

import java.util.List;

public record ActivityWithSuggestionsDTO(
//        ActivitiesReportDTO activity,
        String currentBandEn,
        String currentBandAr,
        List<BandSuggestionDTO> suggestions
) {}