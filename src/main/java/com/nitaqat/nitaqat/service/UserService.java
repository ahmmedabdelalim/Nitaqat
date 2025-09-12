package com.nitaqat.nitaqat.service;

import com.nitaqat.nitaqat.dto.AuthorizationRequest;
import com.nitaqat.nitaqat.dto.SignupRequest;
import com.nitaqat.nitaqat.entity.User;
import com.nitaqat.nitaqat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;


//    private final MessageSource messageSource;



    public void signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(false);
        user.setRole("user");
        user.setName(request.getName());
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);
        emailService.notifyAdminOfNewUser(user.getEmail());
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    public boolean isUserAuthorized(Long userId, String pageName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("auth.user_not_found"));

        switch (pageName.toLowerCase()) {
            case "report":
                return user.isProfessions_active();
            case "calculation":
                return user.isActivity_active();
            default:
                throw new IllegalArgumentException("auth.unknown_page");
        }
    }

}
