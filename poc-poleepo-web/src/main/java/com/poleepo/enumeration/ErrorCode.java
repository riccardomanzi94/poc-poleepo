package com.poleepo.enumeration;

import lombok.Getter;

@Getter
public enum ErrorCode {
    GENERIC(1, "Internal error"),
    MISSING_REQUIRED_FIELD(2, "Missing required field"),
    CONFIGURATION_NOT_VALID(3, "Configuration not valid"),
    PRODUCT_NOT_CREATED(4, "Product not created"),
    PRODUCT_NOT_UPDATED(5, "Product not updated"),
    CONFIGURATION_NOT_FOUND(6, "Configuration not found");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
