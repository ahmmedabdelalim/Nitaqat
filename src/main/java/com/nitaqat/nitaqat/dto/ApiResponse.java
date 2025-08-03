package com.nitaqat.nitaqat.dto;

public class ApiResponse {

    private boolean success;
    private String message;
    private int code;
    private String token;


    // ✅ Proper full constructor
    public ApiResponse(boolean success, String message, int code, String token) {
        this.success = success;
        this.message = message;
        this.code = code;
        this.token = token;
    }

    // Optional 3-arg constructor
    public ApiResponse(boolean success, String message, int code) {
        this(success, message, code, null);
    }

    // Optional constructor for message + token only
    public ApiResponse(boolean success, String message, String token) {
        this.success = success;
        this.message = message;
        this.code = success ? 200 : 500;
        this.token = token;
    }




    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
