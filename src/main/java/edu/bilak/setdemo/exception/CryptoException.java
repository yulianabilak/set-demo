package edu.bilak.setdemo.exception;

/**
 * @author Yuliana
 * @version 1.0.0
 * @project set-demo
 * @class CryptoException
 * @since 03/06/2025 — 21.36
 **/
public class CryptoException extends RuntimeException {
    public CryptoException(String message, Throwable cause) {
        super(message, cause);
    }

    public CryptoException(String message) {
        super(message);
    }
}
