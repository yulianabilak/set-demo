package edu.bilak.setdemo.model;

import java.time.LocalDateTime;

/**
 * @author Yuliana
 * @version 1.0.0
 * @project set-demo
 * @class SETMessage
 * @since 03/06/2025 — 19.07
 **/
public class SETMessage {
    private String messageType;
    private byte[] encryptedData;
    private byte[] signature;
    private byte[] encryptedSessionKey;
    private byte[] hmac;
    private String senderInfo;
    private LocalDateTime timestamp;

    public SETMessage() {
    }

    public SETMessage(String messageType, byte[] encryptedData, byte[] signature, byte[] encryptedSessionKey, byte[] hmac, String senderInfo, LocalDateTime timestamp) {
        this.messageType = messageType;
        this.encryptedData = encryptedData;
        this.signature = signature;
        this.encryptedSessionKey = encryptedSessionKey;
        this.hmac = hmac;
        this.senderInfo = senderInfo;
        this.timestamp = timestamp;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public byte[] getEncryptedData() {
        return encryptedData;
    }

    public void setEncryptedData(byte[] encryptedData) {
        this.encryptedData = encryptedData;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public byte[] getEncryptedSessionKey() {
        return encryptedSessionKey;
    }

    public void setEncryptedSessionKey(byte[] encryptedSessionKey) {
        this.encryptedSessionKey = encryptedSessionKey;
    }

    public byte[] getHmac() {
        return hmac;
    }

    public void setHmac(byte[] hmac) {
        this.hmac = hmac;
    }

    public String getSenderInfo() {
        return senderInfo;
    }

    public void setSenderInfo(String senderInfo) {
        this.senderInfo = senderInfo;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
