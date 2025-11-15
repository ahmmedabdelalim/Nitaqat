package com.nitaqat.nitaqat.controller;

import com.nitaqat.nitaqat.aspect.LogUserAction;
import com.nitaqat.nitaqat.dto.ApiResponse;
import com.nitaqat.nitaqat.dto.AuthorizationRequest;
import com.nitaqat.nitaqat.security.JwtUtils;
import com.nitaqat.nitaqat.service.ActivityService;
import com.nitaqat.nitaqat.service.ExcelImportService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;

@RestController
public class ImportController {

    private final ExcelImportService excelImportService;
    private final MessageSource messageSource;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private ActivityService userActivitySummaryService;

    public ImportController(ExcelImportService excelImportService, MessageSource messageSource) {
        this.excelImportService = excelImportService;
        this.messageSource = messageSource;
    }

    @LogUserAction(action = "Import Data")
    @PostMapping("/api/import")
    public ResponseEntity<ApiResponse> importExcel(
            @RequestParam("type") String type,
            @RequestParam(value = "activityId", required = false) Long activityId, // ✅ Optional parameter
            @RequestParam("file") MultipartFile file,
            Locale locale,
            HttpServletRequest httpServletRequest
    ) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, messageSource.getMessage("file.missing", null, locale), 400));
            }

            // ✅ Extract user ID from JWT
            String header = httpServletRequest.getHeader("Authorization");
            if (header == null || !header.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "auth.missing_token", 401));
            }

            String token = header.substring(7);
            Long userId = jwtUtils.extractUserId(token);

            // ✅ Handle types
            switch (type.toLowerCase()) {
                case "activities":
                    excelImportService.importActivitiesFromExcel(file, userId);
                    break;

                case "professions":
                    // ✅ Check if activityId was provided
                    if (activityId == null) {
                        return ResponseEntity.badRequest()
                                .body(new ApiResponse(false, "activityId is required for professions import", 400));
                    }
                    excelImportService.importProfessionsFromExcel(file, userId, activityId);
                    break;

                case "saudization_percentage":
                    excelImportService.importSaudizationPercentage(file);
                    break;

                default:
                    return ResponseEntity.badRequest()
                            .body(new ApiResponse(false, messageSource.getMessage("unsupported.type", new Object[]{type}, locale), 400));
            }

            userActivitySummaryService.incrementUploadCount(userId);

            return ResponseEntity.ok(new ApiResponse(true, messageSource.getMessage("import.success", null, locale), 200));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(400)
                    .body(new ApiResponse(false, messageSource.getMessage("import.failed", null, locale), 400));
        }
    }

}