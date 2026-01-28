package com.nitaqat.nitaqat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class RedisSessionService {

    private final RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    public RedisSessionService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // ✅ Saves session + backup copy that never expires
    public void saveSession(String key, ActiveSession session) {
        // expiring session key
        redisTemplate.opsForValue().set(key, session);
        redisTemplate.expire(key, Duration.ofMinutes(30));

        // backup (never expires)
        redisTemplate.opsForValue().set(key + "_BACKUP", session);
    }

    // Standard getter
    public ActiveSession getSession(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return value instanceof ActiveSession ? (ActiveSession) value : null;
    }


    public ActiveSession getSessionBackup(Long userId) {
        String backupKey = "USER_SESSION_" + userId + "_BACKUP";
        Object rawValue = redisTemplate.opsForValue().get(backupKey);

        System.out.println("🔥 EXPIRED session raw: " + rawValue);

        // ✅ Convert JSON/Map -> ActiveSession
        try {
            if (rawValue != null) {
                ActiveSession session =
                        objectMapper.convertValue(rawValue, ActiveSession.class);

                System.out.println("🔥 EXPIRED converted session: " + session);
                return session;
            }
        } catch (Exception e) {
            System.out.println("❌ ERROR converting session: " + e.getMessage());
        }

        return null;
    }
    // Refresh activity — update main + backup
    public void refreshSessionActivity(String key) {
        ActiveSession session = getSession(key);
        if (session != null) {
            session.setLastActivityAt(LocalDateTime.now());

            redisTemplate.opsForValue().set(key, session);
            redisTemplate.expire(key, Duration.ofMinutes(30));

            redisTemplate.opsForValue().set(key + "_BACKUP", session);
        }
    }

    public void deleteSession(String key) {
        redisTemplate.delete(key);
        redisTemplate.delete(key + "_BACKUP");
    }

    public static class ActiveSession implements Serializable {
        private Long userId;
        private String username;
        private LocalDateTime loginAt;
        private LocalDateTime lastActivityAt;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public LocalDateTime getLoginAt() { return loginAt; }
        public void setLoginAt(LocalDateTime loginAt) { this.loginAt = loginAt; }

        public LocalDateTime getLastActivityAt() { return lastActivityAt; }
        public void setLastActivityAt(LocalDateTime lastActivityAt) { this.lastActivityAt = lastActivityAt; }
    }

    public void updateLastActivityByKey(String sessionKey) {
        if (sessionKey == null) return;

        try {
            Object raw = redisTemplate.opsForValue().get(sessionKey);
            ActiveSession session = null;

            if (raw != null) {
                // ✅ Session exists normally → update last activity
                session = objectMapper.convertValue(raw, ActiveSession.class);
            } else {
                // ✅ Session expired → check backup
                String backupKey = sessionKey + "_BACKUP";
                Object rawBackup = redisTemplate.opsForValue().get(backupKey);

                if (rawBackup != null) {
                    // ✅ Backup exists → create new session with NEW login time
                    ActiveSession old = objectMapper.convertValue(rawBackup, ActiveSession.class);

                    session = new ActiveSession();
                    session.setUserId(old.getUserId());
                    session.setUsername(old.getUsername());
                    session.setLoginAt(LocalDateTime.now());     // ✅ NEW login
                    session.setLastActivityAt(LocalDateTime.now());

                    // ✅ Remove old backup to avoid duplicated duration
                    redisTemplate.delete(backupKey);
                } else {
                    // ✅ No main session, no backup → user has no session
                    return;
                }
            }

            // ✅ Update lastActivity and save new TTL
            session.setLastActivityAt(LocalDateTime.now());

            redisTemplate.opsForValue().set(sessionKey, session);
            redisTemplate.expire(sessionKey, Duration.ofMinutes(30));

            // ✅ Create new backup
            redisTemplate.opsForValue().set(sessionKey + "_BACKUP", session);

        } catch (Exception ex) {
            System.err.println("Failed to update lastActivity for " + sessionKey + ": " + ex.getMessage());
        }
    }

}

