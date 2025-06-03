package edu.bilak.setdemo.dto;

/**
 * @author Yuliana
 * @version 1.0.0
 * @project set-demo
 * @class ApiResponse
 * @since 03/06/2025 — 20.30
 **/
public class ApiResponse<T> {
    private final String status;
    private final String message;
    private final T data;

    public ApiResponse(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
