package com.nitaqat.nitaqat.controller;

import com.nitaqat.nitaqat.dto.ApiResponse;
import com.nitaqat.nitaqat.service.ExcelImportService;
import org.springframework.cglib.core.Local;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;

@RestController
public class ImportController {

    private final ExcelImportService excelImportService;
    private final MessageSource messageSource;

    public ImportController(ExcelImportService excelImportService, MessageSource messageSource) {
        this.excelImportService = excelImportService;
        this.messageSource = messageSource;
    }

    @PostMapping("/api/import")
    public ResponseEntity<ApiResponse> importExcel(
            @RequestParam("type") String type,
            @RequestParam("file") MultipartFile file,
            Locale locale

    ) {
        try {
//            System.out.println("Received request to /api/import with type: " + type + ", file: " + (file != null ? file.getOriginalFilename() : "null"));
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, messageSource.getMessage("file.missing", null, locale), 400));
            }

            if("activities".equalsIgnoreCase(type))
            {
                excelImportService.importActivitiesFromExcel(file);
            }
            else if("professions".equalsIgnoreCase(type))
            {
                excelImportService.importProfessionsFromExcel(file);
            }
            else if("saudization_percentage".equalsIgnoreCase(type))
            {
                excelImportService.importSaudizationPercentage(file);
            }
            else
            {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, messageSource.getMessage("unsupported.type", new Object[]{type}, locale), 400));

            }

            return ResponseEntity.ok(new ApiResponse(true, messageSource.getMessage("import.success", null, locale), 200));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(new ApiResponse(false, messageSource.getMessage("import.failed", new Object[]{e.getMessage()}, locale), 500));
        }
    }
}