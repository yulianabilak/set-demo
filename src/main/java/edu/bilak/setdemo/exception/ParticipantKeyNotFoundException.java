package edu.bilak.setdemo.exception;

/**
 * @author Yuliana
 * @version 1.0.0
 * @project set-demo
 * @class ParticipantKeyNotFoundException
 * @since 03/06/2025 — 21.27
 **/
public class ParticipantKeyNotFoundException extends RuntimeException {
    public ParticipantKeyNotFoundException(String name) {
        super("Ключі учасника " + name + " не знайдені");
    }
}
