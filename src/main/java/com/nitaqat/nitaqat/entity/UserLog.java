package com.nitaqat.nitaqat.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_logs")
public class UserLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String username;
    private String sessionKey;
    private String action; // LOGIN, LOGOUT, TIMEOUT, PAGE_VISIT, UPLOAD, etc.
    private String page;
    private String ip;
    private String userAgent;
    private LocalDateTime timestamp;

    public UserLog() {}

    public UserLog(Long id, Long userId, String username, String sessionKey, String action,
                   String page, String ip, String userAgent, LocalDateTime timestamp) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.sessionKey = sessionKey;
        this.action = action;
        this.page = page;
        this.ip = ip;
        this.userAgent = userAgent;
        this.timestamp = timestamp;
    }

    // Getters & Setters...
}
