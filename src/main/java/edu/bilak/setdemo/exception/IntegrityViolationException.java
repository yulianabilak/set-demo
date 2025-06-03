package edu.bilak.setdemo.exception;

/**
 * @author Yuliana
 * @version 1.0.0
 * @project set-demo
 * @class IntegrityViolationException
 * @since 03/06/2025 — 21.28
 **/
public class IntegrityViolationException extends RuntimeException {
    public IntegrityViolationException(String message) {
        super(message);
    }
}
