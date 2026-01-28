package com.nitaqat.nitaqat.service;


import com.nitaqat.nitaqat.entity.Activity;
import com.nitaqat.nitaqat.entity.Profession;
import com.nitaqat.nitaqat.entity.SaudizationPercentage;
import com.nitaqat.nitaqat.repository.ActivityRepository;
import com.nitaqat.nitaqat.repository.ProfessionsRepository;
import com.nitaqat.nitaqat.repository.SaudizationPercentageRespository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExportService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ProfessionsRepository professionRepository;

    @Autowired
    private SaudizationPercentageRespository saudizationPercentageRespository;

    public ByteArrayInputStream exportUserData(Long userId) throws IOException {

        List<Activity> activities = activityRepository.findByUserId(userId);
        List<Profession> professions = professionRepository.findByUserId(userId);

        XSSFWorkbook workbook = new XSSFWorkbook();

        // ============================================
        // SHEET 1 — ACTIVITIES
        // ============================================
        XSSFSheet sheet1 = workbook.createSheet("Activities");

        Row header1 = sheet1.createRow(0);
//        header1.createCell(0).setCellValue("ID");
        header1.createCell(0).setCellValue("Name");
        header1.createCell(1).setCellValue("Company Code");
        header1.createCell(2).setCellValue("Percentage");
        header1.createCell(3).setCellValue("Low Green");
        header1.createCell(4).setCellValue("Middle Green");
        header1.createCell(5).setCellValue("High Green");
        header1.createCell(6).setCellValue("Platinum");

        int r1 = 1;
        for (Activity a : activities) {
            Row row = sheet1.createRow(r1++);
//            row.createCell(0).setCellValue(a.getId());
            row.createCell(0).setCellValue(a.getName());
            row.createCell(1).setCellValue(a.getCompanyCode());
            row.createCell(2).setCellValue(a.getPercentage() != null ? a.getPercentage() : 0);
            row.createCell(3).setCellValue(a.getLowGreen() != null ? a.getLowGreen() : 0);
            row.createCell(4).setCellValue(a.getMiddelGreen() != null ? a.getMiddelGreen() : 0);
            row.createCell(5).setCellValue(a.getHighGreen() != null ? a.getHighGreen() : 0);
            row.createCell(6).setCellValue(a.getPlatinum() != null ? a.getPlatinum() : 0);
        }

        // ============================================
        // SHEET 2 — PROFESSIONS
        // ============================================
        XSSFSheet sheet2 = workbook.createSheet("Professions");

        Row header2 = sheet2.createRow(0);
//        header2.createCell(0).setCellValue("ID");
        header2.createCell(0).setCellValue("Activity Name");
        header2.createCell(1).setCellValue("Employee Code");
        header2.createCell(2).setCellValue("Employee Name");
        header2.createCell(3).setCellValue("Nationality");
        header2.createCell(4).setCellValue("Company Code");
        header2.createCell(5).setCellValue("Company Name");
        header2.createCell(6).setCellValue("Border Number");
        header2.createCell(7).setCellValue("ID Number");
        header2.createCell(8).setCellValue("Job");
        header2.createCell(9).setCellValue("Residence Expire Date");
        header2.createCell(10).setCellValue("Entry Date Into Kingdom");
        header2.createCell(11).setCellValue("Work Type");

        int r2 = 1;
        for (Profession p : professions) {
            Row row = sheet2.createRow(r2++);
//            row.createCell(0).setCellValue(p.getId());
            row.createCell(0).setCellValue(p.getActivity().getName());
            row.createCell(1).setCellValue(p.getEmployeeCode());
            row.createCell(2).setCellValue(p.getEmployeeName());
            row.createCell(3).setCellValue(p.getNationality());
            row.createCell(4).setCellValue(p.getCompanyCode());
            row.createCell(5).setCellValue(p.getCompanyName());
            row.createCell(6).setCellValue(p.getBorderNumber());
            row.createCell(7).setCellValue(p.getIdNumber());
            row.createCell(8).setCellValue(p.getJob());
            row.createCell(9).setCellValue(p.getResidenceExpireDate());
            row.createCell(10).setCellValue(p.getDateOfEntryIntoTheKingdom());
            row.createCell(11).setCellValue(p.getWorkType());
        }

        // ============================================
        // WRITE TO OUTPUT STREAM
        // ============================================
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

    public ByteArrayInputStream exportSaudizationPercentage() throws IOException
    {
        List <SaudizationPercentage> saudizationPercentages = saudizationPercentageRespository.findAll();

        XSSFWorkbook workbook = new XSSFWorkbook();

        XSSFSheet sheet1 = workbook.createSheet("Activities");

        Row header1 = sheet1.createRow(0);
//        header1.createCell(0).setCellValue("ID");
        header1.createCell(0).setCellValue("Job");
        header1.createCell(1).setCellValue("Job Ar");
        header1.createCell(2).setCellValue("Job En");
        header1.createCell(3).setCellValue("saudization Catageory");
        header1.createCell(4).setCellValue("saudization Catageory Ar");
        header1.createCell(5).setCellValue("saudization Percentage");
        header1.createCell(6).setCellValue("emp Threshold");

        int r1 = 1;
        for (SaudizationPercentage a : saudizationPercentages) {
            Row row = sheet1.createRow(r1++);

            row.createCell(0).setCellValue(a.getJob());
            row.createCell(1).setCellValue(a.getJobAr());
            row.createCell(2).setCellValue(a.getJobEn());
            row.createCell(3).setCellValue(a.getSaudizationCatageory());
            row.createCell(4).setCellValue(a.getSaudizationCatageoryAr());
            row.createCell(5).setCellValue(a.getSaudizationPercentage()!= null ? a.getSaudizationPercentage() : 0);
            row.createCell(6).setCellValue(a.getEmpThreshold()!= null ? a.getEmpThreshold() : 0);
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return new ByteArrayInputStream(out.toByteArray());
    }
}
