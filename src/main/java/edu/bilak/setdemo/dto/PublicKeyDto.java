package edu.bilak.setdemo.dto;

/**
 * @author Yuliana
 * @version 1.0.0
 * @project set-demo
 * @class PublicKeyDto
 * @since 03/06/2025 — 20.35
 **/
public class PublicKeyDto {
    private String participantName;
    private String publicKey;

    public PublicKeyDto() {
    }

    public PublicKeyDto(String participantName, String publicKey) {
        this.participantName = participantName;
        this.publicKey = publicKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }
}
