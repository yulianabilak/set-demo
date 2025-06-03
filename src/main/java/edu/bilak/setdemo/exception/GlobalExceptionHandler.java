package edu.bilak.setdemo.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.ZonedDateTime;

/**
 * @author Yuliana
 * @version 1.0.0
 * @project set-demo
 * @class GlobalExceptionHandler
 * @since 03/06/2025 — 21.25
 **/
@ControllerAdvice
public class GlobalExceptionHandler {
    private final static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ParticipantKeyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoKey(ParticipantKeyNotFoundException e, WebRequest req) {
        logger.warn(e.getMessage());
        return getResponse(HttpStatus.NOT_FOUND, req, e.getMessage());
    }

    @ExceptionHandler(IntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleIntegrity(IntegrityViolationException e, WebRequest req) {
        logger.warn("Порушення цілісності даних", e);
        return getResponse(HttpStatus.FORBIDDEN, req, e.getMessage());
    }

    @ExceptionHandler(CryptoException.class)
    public ResponseEntity<ErrorResponse> handleCrypto(CryptoException e, WebRequest request) {
        logger.error("Криптографічна помилка", e);
        return getResponse(HttpStatus.INTERNAL_SERVER_ERROR, request, "Криптографічна помилка: " + e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e, WebRequest request) {
        logger.error("Сталася неочікувана помилка", e);
        return getResponse(HttpStatus.INTERNAL_SERVER_ERROR, request, "Сталася неочікувана помилка");
    }

    private ResponseEntity<ErrorResponse> getResponse(HttpStatus status, WebRequest request, String message) {
        String path = ((ServletWebRequest)request).getRequest().getRequestURI();
        ErrorResponse errorMessage = new ErrorResponse(status.value(), status.getReasonPhrase(), ZonedDateTime.now(), path, message);
        return new ResponseEntity<>(errorMessage, status);
    }
}
