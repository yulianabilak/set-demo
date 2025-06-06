package edu.bilak.setdemo.exception;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.ZonedDateTime;

/**
 * @author Yuliana
 * @version 1.0.0
 * @project set-demo
 * @class ErrorResponse
 * @since 03/06/2025 — 21.24
 **/
public class ErrorResponse {
    private int status;
    private String statusDescription;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime timestamp;
    private String path;
    private String message;

    public ErrorResponse() {
    }

    public ErrorResponse(int status, String statusDescription, ZonedDateTime timestamp, String path, String message) {
        this.status = status;
        this.statusDescription = statusDescription;
        this.timestamp = timestamp;
        this.path = path;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
