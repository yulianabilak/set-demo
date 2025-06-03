package edu.bilak.setdemo.dto;

/**
 * @author Yuliana
 * @version 1.0.0
 * @project set-demo
 * @class ReceiveResultDto
 * @since 03/06/2025 — 20.40
 **/
public class ReceiveResultDto {
    private String decryptedMessage;
    private String sender;

    public ReceiveResultDto() {
    }

    public ReceiveResultDto(String decryptedMessage, String sender) {
        this.decryptedMessage = decryptedMessage;
        this.sender = sender;
    }

    public String getDecryptedMessage() {
        return decryptedMessage;
    }

    public void setDecryptedMessage(String decryptedMessage) {
        this.decryptedMessage = decryptedMessage;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
