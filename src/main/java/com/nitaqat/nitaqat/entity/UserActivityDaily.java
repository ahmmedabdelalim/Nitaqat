package com.nitaqat.nitaqat.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_activity_daily",
        uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "activityDate"}))
public class UserActivityDaily {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String username;
    private LocalDate activityDate;
    private Long totalActiveSeconds = 0L;

    // Getters & Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public LocalDate getActivityDate() { return activityDate; }
    public void setActivityDate(LocalDate activityDate) { this.activityDate = activityDate; }

    public Long getTotalActiveSeconds() { return totalActiveSeconds; }
    public void setTotalActiveSeconds(Long totalActiveSeconds) { this.totalActiveSeconds = totalActiveSeconds; }
}
