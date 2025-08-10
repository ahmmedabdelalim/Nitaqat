package com.nitaqat.nitaqat.service;

import com.nitaqat.nitaqat.entity.Activity;
import com.nitaqat.nitaqat.repository.ActivityRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelImportService {

    private final ActivityRepository activityRepository;

    public ExcelImportService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public void importActivitiesFromExcel(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Uploaded file is empty or missing");
        }

        try (InputStream is = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(is);

            // Try to get "Activities" sheet by name, else use the first sheet
            Sheet sheet = workbook.getSheet("Activities");
            if (sheet == null) {
                sheet = workbook.getSheetAt(0);
            }

            if (sheet == null) {
                throw new RuntimeException("No valid sheet found in the Excel file");
            }

            List<Activity> activities = new ArrayList<>();

            // Skip header row (i = 1)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Activity activity = new Activity();
                activity.setName(getCellValue(row.getCell(0)));
                activity.setCompanyCode(getCellValue(row.getCell(1)));

                String parentIdStr = getCellValue(row.getCell(2));
                activity.setParentId(parentIdStr.isEmpty() ? null : String.valueOf(parentIdStr));

                activities.add(activity);
            }

            if (!activities.isEmpty()) {
                activityRepository.saveAll(activities);
            }
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }
}
