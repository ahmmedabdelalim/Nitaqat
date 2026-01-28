package com.nitaqat.nitaqat.dto;

public class AuthorizationRequest {
    private Long userId;
    private String pageName; // e.g., "professions"
    private String lang;     // e.g., "en" or "ar"

    // getters & setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getPageName() { return pageName; }
    public void setPageName(String pageName) { this.pageName = pageName; }

    public String getLang() { return lang; }
    public void setLang(String lang) { this.lang = lang; }
}
