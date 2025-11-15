package com.nitaqat.nitaqat.controller;

import com.nitaqat.nitaqat.dto.*;
import com.nitaqat.nitaqat.entity.User;
import com.nitaqat.nitaqat.security.JwtUtils;
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


    @LogUserAction(action = "Login")
    @PostMapping("/api/auth/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldError().getDefaultMessage();
            return ResponseEntity.badRequest().body(new ApiResponse(false, errorMessage, 400));
        }

        try {
            Optional<User> user = userService.findByEmail(loginRequest.getEmail());

            if (user.isEmpty() || !passwordEncoder.matches(loginRequest.getPassword(), user.get().getPassword())) {
                return ResponseEntity.status(400).body(new ApiResponse(false, "Invalid email or password", 400));
            }

            if (!user.get().isActive()) {
                return ResponseEntity.status(400).body(new ApiResponse(false, "User not active", 400, null, "pending"));
            }

            String token = jwtUtils.generateJwtToken(user.get().getEmail(), user.get().getId());

            // ✅ Create session object
            RedisSessionService.ActiveSession session = new RedisSessionService.ActiveSession();
            session.setUserId(user.get().getId());
            session.setUsername(user.get().getName());
            session.setLoginAt(LocalDateTime.now());
            session.setLastActivityAt(LocalDateTime.now());

            // ✅ Redis key
            String redisKey = "USER_SESSION_" + user.get().getId();

            // ✅ Save session in Redis
            redisSessionService.saveSession(redisKey, session);

            return ResponseEntity.ok(new ApiResponse(true, "Login successful", 200, token, "active"));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "Login failed: " + e.getMessage(), 500));
        }
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
