package com.nitaqat.nitaqat.service;


import com.nitaqat.nitaqat.entity.*;
import com.nitaqat.nitaqat.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.Optional;

@Service
public class ActivityService {

    private final UserActivitySummaryRepository summaryRepo;
    private final UserActivityDailyRepository dailyRepo;
    private final UserActivityMonthlyRepository monthlyRepo;
    private final UserLogRepository logRepo;
    private final RedisSessionService redisService;
    private final UserRepository userRepository;

    public ActivityService(UserActivitySummaryRepository summaryRepo,
                           UserActivityDailyRepository dailyRepo,
                           UserActivityMonthlyRepository monthlyRepo,
                           UserLogRepository logRepo,
                           RedisSessionService redisService, UserRepository userRepository) {
        this.summaryRepo = summaryRepo;
        this.dailyRepo = dailyRepo;
        this.monthlyRepo = monthlyRepo;
        this.logRepo = logRepo;
        this.redisService = redisService;
        this.userRepository = userRepository;
    }

    @Transactional
    public void onLogout(String sessionKey, String ip, String userAgent) {

        // Extract userId from key: USER_SESSION_5 → 5
        Long userId = Long.valueOf(sessionKey.replace("USER_SESSION_", ""));


        // Try to read last known session from Redis backup
        RedisSessionService.ActiveSession session =
                redisService.getSession(sessionKey) != null
                        ? redisService.getSession(sessionKey)
                        : redisService.getSessionBackup(userId);

        if (session == null) return;

        long durationSec = Duration.between(session.getLoginAt(), session.getLastActivityAt()).toMinutes();
        LocalDateTime endTime = session.getLastActivityAt();
        LocalDate date = endTime.toLocalDate();
        String monthKey = endTime.getYear() + "-" + String.format("%02d", endTime.getMonthValue());

        System.out.println("🔥 SESSION TIME: " + durationSec);



        // ✅ Summary
        UserActivitySummary summary = summaryRepo.findByUserId(session.getUserId())
                .orElse(new UserActivitySummary());
        summary.setUserId(session.getUserId());
        summary.setUsername(session.getUsername());
        summary.setTotalActiveSeconds(summary.getTotalActiveSeconds() + durationSec);
        summary.setLastActiveAt(endTime);
        summaryRepo.save(summary);

        // ✅ Daily
        dailyRepo.findByUserIdAndActivityDate(session.getUserId(), date)
                .ifPresentOrElse(d -> {
                    d.setTotalActiveSeconds(d.getTotalActiveSeconds() + durationSec);
                    dailyRepo.save(d);
                }, () -> {
                    UserActivityDaily d = new UserActivityDaily();
                    d.setUserId(session.getUserId());
                    d.setUsername(session.getUsername());
                    d.setActivityDate(date);
                    d.setTotalActiveSeconds(durationSec);
                    dailyRepo.save(d);
                });

        // ✅ Monthly
        monthlyRepo.findByUserIdAndMonth(session.getUserId(), monthKey)
                .ifPresentOrElse(m -> {
                    m.setTotalActiveSeconds(m.getTotalActiveSeconds() + durationSec);
                    monthlyRepo.save(m);
                }, () -> {
                    UserActivityMonthly m = new UserActivityMonthly();
                    m.setUserId(session.getUserId());
                    m.setUsername(session.getUsername());
                    m.setMonth(monthKey);
                    m.setTotalActiveSeconds(durationSec);
                    monthlyRepo.save(m);
                });

        // ✅ Log entry
        logRepo.save(new UserLog(
                null, session.getUserId(), session.getUsername(),
                sessionKey, "TIMEOUT", null, ip, userAgent, LocalDateTime.now()
        ));
    }


    @Transactional
    public void logAction(Long userId, String username, String action, String page, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        UserLog log = new UserLog(
                null, userId, username, null, action, page, ip, userAgent, LocalDateTime.now()
        );
        logRepo.save(log);
    }


    public void incrementUploadCount(Long userId) {

        Optional<User> user = userRepository.findById(userId);

        UserActivitySummary summary = summaryRepo.findByUserId(userId)
                .orElseGet(() -> {
                    UserActivitySummary s = new UserActivitySummary();
                    s.setUserId(userId);

                    // ✅ Insert username if user exists
                    user.ifPresent(u -> s.setUsername(u.getName()));

                    s.setUploadCount(0L);
                    s.setLastActiveAt(LocalDateTime.now());
                    return s;
                });

        // If summary existed but username is missing, update it
        if (summary.getUsername() == null && user.isPresent()) {
            summary.setUsername(user.get().getName());
            summary.setLastActiveAt(LocalDateTime.now());
        }

        summary.setUploadCount(summary.getUploadCount() + 1);
        summary.setLastActiveAt(LocalDateTime.now());

        summaryRepo.save(summary);
    }

}
