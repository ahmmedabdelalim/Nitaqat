package com.nitaqat.nitaqat.controller;


import com.nitaqat.nitaqat.aspect.LogUserAction;
import com.nitaqat.nitaqat.dto.ApiResponse;
import com.nitaqat.nitaqat.security.JwtUtils;
import com.nitaqat.nitaqat.service.ResetService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ResetController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ResetService resetService;

    @LogUserAction(action = "Reset Data")
    @PostMapping("/reset-data")
    @Transactional
    public ResponseEntity<ApiResponse> resetData(HttpServletRequest request)
    {
        // Check Authorization
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "auth.missing_token", 401));
        }

        // Extract user ID
        String token = header.substring(7);
        Long userId = jwtUtils.extractUserId(token);

        // Reset the user's data
        resetService.resetUserData(userId);

        return ResponseEntity.ok(
                new ApiResponse(true, "User data reset successfully", 200)
        );
    }
}
