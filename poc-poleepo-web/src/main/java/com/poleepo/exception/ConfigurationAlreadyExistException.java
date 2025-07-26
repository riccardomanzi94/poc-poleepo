package com.poleepo.exception;

public class ConfigurationAlreadyExistException extends RuntimeException {
    public ConfigurationAlreadyExistException(String message) {
        super(message);
    }
}
