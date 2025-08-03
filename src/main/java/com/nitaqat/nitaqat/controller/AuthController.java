package com.nitaqat.nitaqat.controller;

import com.nitaqat.nitaqat.dto.LoginRequest;
import com.nitaqat.nitaqat.dto.SignupRequest;
import com.nitaqat.nitaqat.entity.User;
import com.nitaqat.nitaqat.security.JwtUtils;
import com.nitaqat.nitaqat.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.nitaqat.nitaqat.dto.ApiResponse;

import java.util.Optional;


@RestController
public class AuthController {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;





    @PostMapping("/api/auth/signup")
    @ResponseBody
    public ResponseEntity<ApiResponse> signup(@Valid @RequestBody SignupRequest request) {
        try {
            userService.signup(request);
            return ResponseEntity.ok(
                    new ApiResponse(true, "Signup successful. Admin has been notified.", 200)
            );
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(new ApiResponse(false, "Signup failed: " + e.getMessage(), 500));
        }
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest loginRequest , BindingResult bindingResult) {
        if(bindingResult.hasErrors())
        {
            String errorMessage = bindingResult.getFieldError().getDefaultMessage();
            return  ResponseEntity.badRequest().body(new ApiResponse(false, errorMessage , 400));
        }
        try {

                Optional<User> user = userService.findByEmail(loginRequest.getEmail()) ;

                if (user.isEmpty() || !passwordEncoder.matches(loginRequest.getPassword(), user.get().getPassword())) {
                    return ResponseEntity.status(401).body(new ApiResponse(false, "Invalid email or password", 401));
                }

                if (!user.get().isActive()) {
                    return ResponseEntity.status(403).body(new ApiResponse(false, "User not active", 403));
                }

                String token = jwtUtils.generateJwtToken(user.get().getEmail());


                return ResponseEntity.ok(new ApiResponse(true, "Login successful", 200, token));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "Login failed: " + e.getMessage(), 500));
        }
    }



}
