package com.nitaqat.nitaqat.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_activity_monthly",
        uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "month"}))
public class UserActivityMonthly {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String username;
    private String month; // e.g., "2025-10"
    private Long totalActiveSeconds = 0L;

    // Getters & Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public Long getTotalActiveSeconds() { return totalActiveSeconds; }
    public void setTotalActiveSeconds(Long totalActiveSeconds) { this.totalActiveSeconds = totalActiveSeconds; }
}
