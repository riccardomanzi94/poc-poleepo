package com.poleepo.exception;

import com.poleepo.enumeration.ErrorCode;
import com.poleepo.model.response.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class PoleepoExceptionHandler {

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<String>> handleValidationExceptions(
            org.springframework.web.bind.MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(ResponseDto.<String>builder()
                .success(false)
                .error(ErrorCode.MISSING_REQUIRED_FIELD.getCode())
                .message(ErrorCode.MISSING_REQUIRED_FIELD.getMessage())
                .build());
    }

    @ExceptionHandler(ShopNotFoundException.class)
    public ResponseEntity<ResponseDto<String>> handleShopNotFoundException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDto.<String>builder()
                .success(false)
                .error(ErrorCode.CONFIGURATION_NOT_FOUND.getCode())
                .message(ErrorCode.CONFIGURATION_NOT_FOUND.getMessage())
                .build());
    }

    @ExceptionHandler(ConfigurationAlreadyExistException.class)
    public ResponseEntity<ResponseDto<String>> handleConfigurationAlreadyExistException() {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseDto.<String>builder()
                .success(false)
                .error(ErrorCode.CONFIGURATION_NOT_VALID.getCode())
                .message(ErrorCode.CONFIGURATION_NOT_VALID.getMessage())
                .build());
    }

    @ExceptionHandler(GenericException.class)
    public ResponseEntity<ResponseDto<String>> handleGenericException() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDto.<String>builder()
                .success(false)
                .error(ErrorCode.GENERIC.getCode())
                .message(ErrorCode.GENERIC.getMessage())
                .build());
    }

}
