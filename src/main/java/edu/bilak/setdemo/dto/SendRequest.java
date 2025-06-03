package edu.bilak.setdemo.dto;

/**
 * @author Yuliana
 * @version 1.0.0
 * @project set-demo
 * @class SendRequest
 * @since 03/06/2025 — 20.38
 **/
public class SendRequest {
    private String sender;
    private String recipient;
    private String message;

    public SendRequest() {
    }

    public SendRequest(String sender, String recipient, String message) {
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
