package com.nitaqat.nitaqat.controller;

import com.nitaqat.nitaqat.dto.*;
import com.nitaqat.nitaqat.entity.User;
import com.nitaqat.nitaqat.security.JwtUtils;
import com.nitaqat.nitaqat.service.EmailService;
import com.nitaqat.nitaqat.service.RedisSessionService;
import com.nitaqat.nitaqat.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.nitaqat.nitaqat.aspect.LogUserAction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.context.MessageSource;
import java.util.Locale;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@RestController
public class AuthController {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;
    @Autowired
    private MessageSource messageSource;
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final RedisSessionService redisSessionService;

    public AuthController(RedisSessionService redisSessionService) {
        this.redisSessionService = redisSessionService;
    }


    @PostMapping(value = "/api/auth/signup", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<ApiResponse> signup(
            @Valid @RequestBody SignupRequest request,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLang) {

        Locale locale;
        if (request.getLang() != null && !request.getLang().isBlank()) {
            // normalize: en_US -> en-US
            locale = Locale.forLanguageTag(request.getLang().replace('_', '-'));
        } else if (acceptLang != null && !acceptLang.isBlank()) {
            locale = Locale.forLanguageTag(acceptLang);
        } else {
            locale = LocaleContextHolder.getLocale();
        }

        try {
            userService.signup(request);

            String msg = messageSource.getMessage("signup.success", null, locale);
            return ResponseEntity.ok(new ApiResponse(true, msg, 200, null));

        } catch (Exception e) {
            // log the full stack trace for debugging
            log.error("Signup failed for email {}: {}", request.getEmail(), e.getMessage(), e);

            // safe message for the client
            String msg = messageSource.getMessage("signup.failure", null, locale);

            // return response with error details (only short message, not full stack trace)
            return ResponseEntity
                    .status(400)
                    .body(new ApiResponse(false, e.getMessage(), 400, null));
        }
    }


    @LogUserAction(action = "Login OTP Request")
    @PostMapping("/api/auth/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldError().getDefaultMessage();
            return ResponseEntity.badRequest().body(new ApiResponse(false, errorMessage, 400));
        }

        try {
            Optional<User> optionalUser = userService.findByEmail(loginRequest.getEmail());
            if (optionalUser.isEmpty() || !passwordEncoder.matches(loginRequest.getPassword(), optionalUser.get().getPassword())) {
                return ResponseEntity.status(400).body(new ApiResponse(false, "Invalid email or password", 400));
            }

            User user = optionalUser.get();

            if (!user.isActive()) {
                return ResponseEntity.status(400).body(new ApiResponse(false, "User not active", 400, null, "pending"));
            }

            // 🔹 Generate OTP (6 digits)
            String otp = String.valueOf((int) (Math.random() * 900000) + 100000);
            user.setOtpCode(otp);
            user.setOtpExpiresAt(LocalDateTime.now().plusMinutes(5));
            user.setOtpVerified(false);

            userService.save(user);  // update user with otp

            // 🔹 Send OTP via email
            emailService.sendOtpEmail(user.getEmail(), otp);

            return ResponseEntity.ok(new ApiResponse(true, "OTP sent to email", 200, null, "otp_sent"));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "Login failed: " + e.getMessage(), 500));
        }
    }

    @LogUserAction(action = "Verify OTP")
    @PostMapping("/api/auth/verify-otp")
    public ResponseEntity<ApiResponse> verifyOtp(@RequestBody VerifyOtpRequest request) {

        Optional<User> optionalUser = userService.findByEmail(request.getEmail());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(400).body(new ApiResponse(false, "User not found", 400));
        }

        User user = optionalUser.get();

        // ❌ OTP invalid
        if (!request.getOtp().equals(user.getOtpCode())) {
            return ResponseEntity.status(400).body(new ApiResponse(false, "Invalid OTP", 400));
        }

        // ❌ OTP expired
        if (user.getOtpExpiresAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(400).body(new ApiResponse(false, "OTP expired", 400));
        }

        // 🔹 OTP verified
        user.setOtpVerified(true);
        user.setOtpCode(null);
        user.setOtpExpiresAt(null);
        userService.save(user);

        // 🔹 Generate JWT
        String token = jwtUtils.generateJwtToken(user.getEmail(), user.getId());

        // 🔹 Create session in Redis
        RedisSessionService.ActiveSession session = new RedisSessionService.ActiveSession();
        session.setUserId(user.getId());
        session.setUsername(user.getName());
        session.setLoginAt(LocalDateTime.now());
        session.setLastActivityAt(LocalDateTime.now());

        String redisKey = "USER_SESSION_" + user.getId();
        redisSessionService.saveSession(redisKey, session);

        return ResponseEntity.ok(new ApiResponse(true, "OTP Verified and Login successful", 200, token, "active"));
    }



    @LogUserAction(action = "Authorize")
    @PostMapping("/api/auth/authorize")
    public ResponseEntity<ApiResponse> checkAuthorization(@RequestBody AuthorizationRequest request
    , HttpServletRequest httpServletRequest) {
        Locale locale = new Locale(request.getLang());

        // Localize page name from messages_xx.yml
        String localizedPageName = messageSource.getMessage(
                "pages." + request.getPageName().toLowerCase(),
                null,
                locale
        );

        String header = httpServletRequest.getHeader("Authorization");
        if(header == null || !header.startsWith("Bearer "))
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false,"auth.missing_token" , 401));
        }
        String token = header.substring(7);
        Long userId  = jwtUtils.extractUserId(token);

        boolean authorized;
        try {
            authorized = userService.isUserAuthorized(userId, request.getPageName());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), 400));
        }

        String messageKey = authorized ? "auth.success" : "auth.failure";
        String responseMessage = messageSource.getMessage(
                messageKey,
                new Object[]{localizedPageName},
                locale
        );

        if(authorized)
        {
            return ResponseEntity.ok(
                    new ApiResponse(true, responseMessage, 200)
            );
        }
        else
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, responseMessage, 400));
        }
    }


    @GetMapping("api/auth/uploadlist")
    public ResponseEntity<uploadListApiResponse> uploadList(HttpServletRequest httpServletRequest)
    {
        // ----- 1. Check Token -----
        String header = httpServletRequest.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new uploadListApiResponse(false, "auth.missing_token", 401));
        }

        // ----- 2. Extract User ID -----
        String token = header.substring(7);
        Long userId = jwtUtils.extractUserId(token);

        // get
        boolean authorized;

        authorized = userService.isUserAuthorized(userId, "saudization_percentage");



        // ----- 3. Create your list of strings -----
        List<String> items =  new ArrayList<>();
        items.add("activities");
        items.add("professions");

        if(authorized)
        {
            items.add("saudization_percentage");
        }

        // ----- 4. Return response -----
        return ResponseEntity.ok(
                new uploadListApiResponse(true, "success", 200, items)
        );
    }




}
