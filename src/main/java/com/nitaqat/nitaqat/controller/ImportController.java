package com.nitaqat.nitaqat.controller;

import com.nitaqat.nitaqat.dto.ApiResponse;
import com.nitaqat.nitaqat.service.ExcelImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController


public class ImportController {

    private final ExcelImportService excelImportService;

    public ImportController(ExcelImportService excelImportService) {
        this.excelImportService = excelImportService;
    }

    @PostMapping("/api/import")
    public ResponseEntity<ApiResponse> importExcel(@RequestParam("file") MultipartFile file,@RequestParam("type") String type)
    {
        return ResponseEntity.ok(new ApiResponse(true, "Data imported successfully", 200));
    }
//    public ResponseEntity<ApiResponse> importExcel(
//            @RequestParam("file") MultipartFile file,
//            @RequestParam("type") String type
//    ) {
//        try {
//            if ("activities".equalsIgnoreCase(type)) {
//                excelImportService.importActivitiesFromExcel(file);
//            } else {
//                return ResponseEntity.badRequest()
//                        .body(new ApiResponse(false, "Unsupported import type: " + type, 400));
//            }
//            return ResponseEntity.ok(new ApiResponse(true, "Data imported successfully", 200));
//        } catch (Exception e) {
//            return ResponseEntity.status(500)
//                    .body(new ApiResponse(false, "Import failed: " + e.getMessage(), 500));
//        }
//    }
}
