package com.skillforge.exception;

/** Thrown when an uploaded file fails the {@link com.skillforge.service.VirusScanner} check. */
public class MaliciousFileException extends RuntimeException {

    public MaliciousFileException(String message) {
        super(message);
    }
}
