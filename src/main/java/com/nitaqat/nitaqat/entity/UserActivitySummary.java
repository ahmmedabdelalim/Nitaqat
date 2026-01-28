package com.nitaqat.nitaqat.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_activity_summary")
public class UserActivitySummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String username;
    private Long totalActiveSeconds = 0L;
    private LocalDateTime lastActiveAt;
    private Long uploadCount = 0L; // optional

    // Getters & Setters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Long getTotalActiveSeconds() { return totalActiveSeconds; }
    public void setTotalActiveSeconds(Long totalActiveSeconds) { this.totalActiveSeconds = totalActiveSeconds; }

    public LocalDateTime getLastActiveAt() { return lastActiveAt; }
    public void setLastActiveAt(LocalDateTime lastActiveAt) { this.lastActiveAt = lastActiveAt; }

    public Long getUploadCount() { return uploadCount; }
    public void setUploadCount(Long uploadCount) { this.uploadCount = uploadCount; }
}
