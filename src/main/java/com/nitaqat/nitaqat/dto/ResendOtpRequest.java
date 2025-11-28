package com.nitaqat.nitaqat.dto;

import jakarta.validation.constraints.Email;

public class ResendOtpRequest {
    @Email
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
