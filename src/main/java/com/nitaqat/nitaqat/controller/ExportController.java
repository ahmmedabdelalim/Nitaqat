package com.nitaqat.nitaqat.controller;


import com.nitaqat.nitaqat.aspect.LogUserAction;
import com.nitaqat.nitaqat.dto.ApiResponse;
import com.nitaqat.nitaqat.security.JwtUtils;
import com.nitaqat.nitaqat.service.ExportService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
@RestController
@RequestMapping("/api")
public class ExportController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ExportService exportService;

    @LogUserAction(action = "Export Data")
    @GetMapping("/export")
    public ResponseEntity<?> export(HttpServletRequest request) {

        // 1️⃣ Check Authorization header
        String header = request.getHeader("Authorization");
        System.out.println(header);
        if (header == null || !header.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "auth.missing_token", 401));
        }

        try {
            // 2️⃣ Extract token and user ID
            String token = header.substring(7);
            Long userId = jwtUtils.extractUserId(token);

            // 3️⃣ Generate Excel file
            ByteArrayInputStream excel = exportService.exportUserData(userId);

            // 4️⃣ Return as downloadable file
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=user_data.xlsx")
                    .body(excel.readAllBytes());

        } catch (Exception e) {
            // Token invalid or expired
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Unauthorized access", 401));
        }
    }

    @LogUserAction(action = "Export Saudization Percentage")
    @GetMapping("/export-saudization-percentage")
    public ResponseEntity<?> export_saudization_percentage(HttpServletRequest request) {

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Missing token", 401));
        }

        String token = header.substring(7); // 🔹 remove "Bearer "

        try
        {
            if (!jwtUtils.validateJwtToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "Invalid or expired token", 401));
            }
            ByteArrayInputStream excel = exportService.exportSaudizationPercentage();

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=Saudization Percentage.xlsx")
                    .body(excel.readAllBytes());


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
