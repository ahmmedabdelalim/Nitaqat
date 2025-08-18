package com.nitaqat.nitaqat.service;

import com.nitaqat.nitaqat.entity.Activity;
import com.nitaqat.nitaqat.entity.Profession;
import com.nitaqat.nitaqat.entity.SaudizationPercentage;
import com.nitaqat.nitaqat.repository.ActivityRepository;
import com.nitaqat.nitaqat.repository.ProfessionsRepository;
import com.nitaqat.nitaqat.repository.SaudizationPercentageRespository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.chrono.HijrahDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.time.chrono.HijrahDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
public class ExcelImportService {

    private final ActivityRepository activityRepository;
    private final ProfessionsRepository professionsRepository;
    private final SaudizationPercentageRespository saudizationPercentageRespository;

    public ExcelImportService(ActivityRepository activityRepository, ProfessionsRepository professionsRepository, SaudizationPercentageRespository saudizationPercentageRespository) {
        this.activityRepository = activityRepository;
        this.professionsRepository = professionsRepository;
        this.saudizationPercentageRespository = saudizationPercentageRespository;
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

    public void importProfessionsFromExcel (MultipartFile file) throws IOException
    {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Uploaded file is empty or missing");
        }
        try (InputStream is = file.getInputStream())
        {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheet("Professions");
            if (sheet == null) {
                sheet = workbook.getSheetAt(0);
            }
            if (sheet == null) {
                throw new RuntimeException("No valid sheet found in the Excel file");
            }

            List<Profession> professions = new  ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Profession profession = new Profession();
                String employeeCode = getCellValue(row.getCell(0));
                profession.setEmployeeCode(employeeCode.isEmpty() ? null : employeeCode);

                String employeeName = getCellValue(row.getCell(1));
                profession.setEmployeeName(employeeName.isEmpty() ? null : employeeName);

                String nationality = getCellValue(row.getCell(2));
                profession.setNationality(nationality.isEmpty() ? null : nationality);

                String companyCode = getCellValue(row.getCell(3));
                profession.setCompanyCode(companyCode.isEmpty() ? null : companyCode);

                String companyName = getCellValue(row.getCell(4));
                profession.setCompanyName(companyName.isEmpty() ? null : companyName);

                String borderNumber = getCellValue(row.getCell(5));
                profession.setBorderNumber(borderNumber.isEmpty() ? null : borderNumber);

                String idNumber = getCellValue(row.getCell(6));
                profession.setIdNumber(idNumber.isEmpty() ? null : idNumber);

                String job = getCellValue(row.getCell(7));
                profession.setJob(job.isEmpty() ? null : job);

//                String residenceExpireDateStr = getCellValue(row.getCell(8));
//                LocalDate residenceExpireDate = parseHijriDateManual(residenceExpireDateStr);
//                profession.setResidenceExpireDate(residenceExpireDate);
//
//                String dateOfEntryStr = getCellValue(row.getCell(9));
//                LocalDate dateOfEntry = parseHijriDateManual(dateOfEntryStr);
//                profession.setDateOfEntryIntoTheKingdom(dateOfEntry);

//                String residenceExpireDateStr = getCellValue(row.getCell(8));
//                if (residenceExpireDateStr.isEmpty()) {
//                    profession.setResidenceExpireDate(null);
//                } else {
//                    profession.setResidenceExpireDate(LocalDate.parse(residenceExpireDateStr));
//                }
//
//                String dateOfEntryStr = getCellValue(row.getCell(9));
//                if (dateOfEntryStr.isEmpty()) {
//                    profession.setDateOfEntryIntoTheKingdom(null);
//                } else {
//                    profession.setDateOfEntryIntoTheKingdom(LocalDate.parse(dateOfEntryStr));
//                }
                String residenceExpireDateStr = getCellValue(row.getCell(8));
                if (residenceExpireDateStr.isEmpty()) {
                    profession.setResidenceExpireDate(null);
                } else {
                    profession.setResidenceExpireDate(residenceExpireDateStr); // store as String
                }

                String dateOfEntryStr = getCellValue(row.getCell(9));
                if (dateOfEntryStr.isEmpty()) {
                    profession.setDateOfEntryIntoTheKingdom(null);
                } else {
                    profession.setDateOfEntryIntoTheKingdom(dateOfEntryStr); // store as String
                }



                String workType = getCellValue(row.getCell(10));
                profession.setWorkType(workType.isEmpty() ? null : workType);

                professions.add(profession);
            }

            if (!professions.isEmpty()) {
                professionsRepository.saveAll(professions);
            }

        }
    }

    public void importSaudizationPercentage (MultipartFile file) throws IOException
    {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Uploaded file is empty or missing");
        }
        try (InputStream is = file.getInputStream())
        {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheet("Saudized Job Title");
            if (sheet == null) {
                sheet = workbook.getSheetAt(0);
            }
            if (sheet == null) {
                throw new RuntimeException("No valid sheet found in the Excel file");
            }

            List <SaudizationPercentage> saudizationPercentages = new  ArrayList<>();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                SaudizationPercentage saudizationPercentage = new SaudizationPercentage();

//                String primaryColumn = getCellValue(row.getCell(0));
//                saudizationPercentage.setPrimaryColumn(primaryColumn.isEmpty() ? null : Long.valueOf(primaryColumn));

                saudizationPercentage.setPrimaryColumn(null);

                String job = getCellValue(row.getCell(1));
                saudizationPercentage.setJob(job.isEmpty()? null : job);

                String job_ar = getCellValue(row.getCell(2));
                saudizationPercentage.setJobAr(job_ar.isEmpty()? null : job_ar);

                String job_en = getCellValue(row.getCell(3));
                saudizationPercentage.setJobEn(job_en.isEmpty()? null : job_en);

                String Saudization_Catageory = getCellValue(row.getCell(4));
                saudizationPercentage.setSaudizationCatageory(Saudization_Catageory.isEmpty()? null : Saudization_Catageory);

                String Saudization_Percentage = getCellValue(row.getCell(5));
                saudizationPercentage.setSaudizationPercentage(Saudization_Percentage.isEmpty()? null : Long.valueOf(Saudization_Percentage));

                String Saudization_CatageoryAr = getCellValue(row.getCell(6));
                saudizationPercentage.setSaudizationCatageoryAr(Saudization_CatageoryAr.isEmpty()? null : Saudization_CatageoryAr);

                String EmpThreshold = getCellValue(row.getCell(7));
                saudizationPercentage.setEmpThreshold(EmpThreshold.isEmpty()? null : Long.valueOf(EmpThreshold));

                saudizationPercentages.add(saudizationPercentage);
            }
            if (!saudizationPercentages.isEmpty()) {
                saudizationPercentageRespository.saveAll(saudizationPercentages);
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

    public LocalDate parseHijriDateManual(String hijriDateStr) {
        if (hijriDateStr == null || hijriDateStr.trim().isEmpty()) {
            return null;
        }

        try {
            String cleanDate = hijriDateStr.replace("هـ", "").trim();  // remove Arabic letter

            String[] parts = cleanDate.split("/");

            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid Hijri date format");
            }

            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);

            HijrahDate hijrahDate = HijrahDate.of(year, month, day);
            return LocalDate.from(hijrahDate);

        } catch (Exception e) {
            System.err.println("Error parsing Hijri date: " + hijriDateStr + " | " + e.getMessage());
            return null;
        }
    }
}
