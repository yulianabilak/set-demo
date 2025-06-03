package edu.bilak.setdemo.dto;

import edu.bilak.setdemo.model.SETMessage;

/**
 * @author Yuliana
 * @version 1.0.0
 * @project set-demo
 * @class ReceiveRequest
 * @since 03/06/2025 — 20.42
 **/
public class ReceiveRequest {
    private String recipient;
    private SETMessage message;

    public ReceiveRequest() {
    }

    public ReceiveRequest(String recipient, SETMessage message) {
        this.recipient = recipient;
        this.message = message;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public SETMessage getMessage() {
        return message;
    }

    public void setMessage(SETMessage message) {
        this.message = message;
    }
}
