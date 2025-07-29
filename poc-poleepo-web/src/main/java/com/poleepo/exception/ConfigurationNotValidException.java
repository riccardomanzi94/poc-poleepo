package com.poleepo.exception;

public class ConfigurationNotValidException extends RuntimeException {
    public ConfigurationNotValidException(String message) {
        super(message);
    }
}
