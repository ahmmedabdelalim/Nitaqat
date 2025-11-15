package com.nitaqat.nitaqat.dto;

public class uploadListApiResponse {

    private boolean success;
    private String message;
    private int status;
    private Object data;   // <--- important for returning list

    public uploadListApiResponse(boolean success, String message, int status, Object data) {
        this.success = success;
        this.message = message;
        this.status = status;
        this.data = data;
    }

    public uploadListApiResponse(boolean success, String message, int status) {
        this.success = success;
        this.message = message;
        this.status = status;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
