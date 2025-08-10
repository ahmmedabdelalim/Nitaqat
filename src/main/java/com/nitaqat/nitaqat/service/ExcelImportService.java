package com.nitaqat.nitaqat.service;

import com.nitaqat.nitaqat.entity.Activity;
import com.nitaqat.nitaqat.repository.ActivityRepository;
import jakarta.mail.Multipart;
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


    public void importActivitiesFromExcel (MultipartFile file) throws IOException{
        try (InputStream is = file.getInputStream())
        {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheet(String.valueOf(0));
            List<Activity> activities = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++)
            {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Activity activity = new Activity();
                activity.setName(getCellValue(row.getCell(0)));
                activity.setCompanyCode(getCellValue(row.getCell(1)));
                String parentIdStr = getCellValue(row.getCell(2));
                activity.setParentId(parentIdStr.isEmpty() ? null : Long.valueOf(parentIdStr));

                activities.add(activity);
            }
            activityRepository.saveAll(activities);
        }
    }


    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }
}
