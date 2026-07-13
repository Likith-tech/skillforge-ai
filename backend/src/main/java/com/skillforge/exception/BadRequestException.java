package com.skillforge.exception;

/**
 * Generic 400 for Phase 2 validation failures that don't warrant their own
 * exception type (job closed, duplicate application, no resume on file, ...).
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
