package com.poleepo.exception;

import com.poleepo.enumeration.ErrorCode;
import com.poleepo.model.response.ResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PoleepoExceptionHandlerTest {

    @InjectMocks
    private PoleepoExceptionHandler poleepoExceptionHandler;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        // Setup common mocks if needed
    }

    @Test
    void handleValidationExceptions_WhenMethodArgumentNotValidException_ShouldReturnBadRequestWithCorrectErrorCode() {
        // Given
        FieldError fieldError = new FieldError("configurationRequest", "shopId", "must not be null");

        // When
        ResponseEntity<ResponseDto<String>> response = poleepoExceptionHandler.handleValidationExceptions(methodArgumentNotValidException);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        ResponseDto<String> responseBody = response.getBody();
        assertFalse(responseBody.isSuccess());
        assertEquals(ErrorCode.MISSING_REQUIRED_FIELD.getCode(), responseBody.getError());
        assertEquals(ErrorCode.MISSING_REQUIRED_FIELD.getMessage(), responseBody.getMessage());
        assertNull(responseBody.getData());
    }

    @Test
    void handleValidationExceptions_VerifyErrorCodeValues() {
        // When
        ResponseEntity<ResponseDto<String>> response = poleepoExceptionHandler.handleValidationExceptions(methodArgumentNotValidException);

        // Then
        assertNotNull(response.getBody());
        ResponseDto<String> responseBody = response.getBody();
        assertEquals(2, responseBody.getError()); // MISSING_REQUIRED_FIELD code
        assertEquals("Missing required field", responseBody.getMessage()); // MISSING_REQUIRED_FIELD message
    }

    @Test
    void handleShopNotFoundException_ShouldReturnNotFoundWithCorrectErrorCode() {
        // When
        ResponseEntity<ResponseDto<String>> response = poleepoExceptionHandler.handleShopNotFoundException();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());

        ResponseDto<String> responseBody = response.getBody();
        assertFalse(responseBody.isSuccess());
        assertEquals(ErrorCode.CONFIGURATION_NOT_FOUND.getCode(), responseBody.getError());
        assertEquals(ErrorCode.CONFIGURATION_NOT_FOUND.getMessage(), responseBody.getMessage());
        assertNull(responseBody.getData());
    }

    @Test
    void handleShopNotFoundException_VerifyErrorCodeValues() {
        // When
        ResponseEntity<ResponseDto<String>> response = poleepoExceptionHandler.handleShopNotFoundException();

        // Then
        assertNotNull(response.getBody());
        ResponseDto<String> responseBody = response.getBody();
        assertEquals(6, responseBody.getError()); // CONFIGURATION_NOT_FOUND code
        assertEquals("Configuration not found", responseBody.getMessage()); // CONFIGURATION_NOT_FOUND message
    }

    @Test
    void handleConfigurationAlreadyExistException_ShouldReturnConflictWithCorrectErrorCode() {
        // When
        ResponseEntity<ResponseDto<String>> response = poleepoExceptionHandler.handleConfigurationAlreadyExistException();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());

        ResponseDto<String> responseBody = response.getBody();
        assertFalse(responseBody.isSuccess());
        assertEquals(ErrorCode.CONFIGURATION_NOT_VALID.getCode(), responseBody.getError());
        assertEquals(ErrorCode.CONFIGURATION_NOT_VALID.getMessage(), responseBody.getMessage());
        assertNull(responseBody.getData());
    }

    @Test
    void handleConfigurationAlreadyExistException_VerifyErrorCodeValues() {
        // When
        ResponseEntity<ResponseDto<String>> response = poleepoExceptionHandler.handleConfigurationAlreadyExistException();

        // Then
        assertNotNull(response.getBody());
        ResponseDto<String> responseBody = response.getBody();
        assertEquals(3, responseBody.getError()); // CONFIGURATION_NOT_VALID code
        assertEquals("Configuration not valid", responseBody.getMessage()); // CONFIGURATION_NOT_VALID message
    }

    @Test
    void handleGenericException_ShouldReturnInternalServerErrorWithCorrectErrorCode() {
        // When
        ResponseEntity<ResponseDto<String>> response = poleepoExceptionHandler.handleGenericException();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());

        ResponseDto<String> responseBody = response.getBody();
        assertFalse(responseBody.isSuccess());
        assertEquals(ErrorCode.GENERIC.getCode(), responseBody.getError());
        assertEquals(ErrorCode.GENERIC.getMessage(), responseBody.getMessage());
        assertNull(responseBody.getData());
    }

    @Test
    void handleGenericException_VerifyErrorCodeValues() {
        // When
        ResponseEntity<ResponseDto<String>> response = poleepoExceptionHandler.handleGenericException();

        // Then
        assertNotNull(response.getBody());
        ResponseDto<String> responseBody = response.getBody();
        assertEquals(1, responseBody.getError()); // GENERIC code
        assertEquals("Internal error", responseBody.getMessage()); // GENERIC message
    }

    @Test
    void allExceptionHandlers_ShouldReturnResponseWithSuccessFalse() {
        // Test that all exception handlers return success = false

        // Validation exception
        ResponseEntity<ResponseDto<String>> validationResponse = poleepoExceptionHandler.handleValidationExceptions(methodArgumentNotValidException);
        assertNotNull(validationResponse.getBody());
        assertFalse(validationResponse.getBody().isSuccess());

        // Shop not found exception
        ResponseEntity<ResponseDto<String>> shopNotFoundResponse = poleepoExceptionHandler.handleShopNotFoundException();
        assertNotNull(shopNotFoundResponse.getBody());
        assertFalse(shopNotFoundResponse.getBody().isSuccess());

        // Configuration already exist exception
        ResponseEntity<ResponseDto<String>> configExistResponse = poleepoExceptionHandler.handleConfigurationAlreadyExistException();
        assertNotNull(configExistResponse.getBody());
        assertFalse(configExistResponse.getBody().isSuccess());

        // Generic exception
        ResponseEntity<ResponseDto<String>> genericResponse = poleepoExceptionHandler.handleGenericException();
        assertNotNull(genericResponse.getBody());
        assertFalse(genericResponse.getBody().isSuccess());
    }

    @Test
    void allExceptionHandlers_ShouldReturnNullData() {
        // Test that all exception handlers return data = null

        ResponseEntity<ResponseDto<String>> validationResponse = poleepoExceptionHandler.handleValidationExceptions(methodArgumentNotValidException);
        assertNotNull(validationResponse.getBody());
        assertNull(validationResponse.getBody().getData());

        ResponseEntity<ResponseDto<String>> shopNotFoundResponse = poleepoExceptionHandler.handleShopNotFoundException();
        assertNotNull(shopNotFoundResponse.getBody());
        assertNull(shopNotFoundResponse.getBody().getData());

        ResponseEntity<ResponseDto<String>> configExistResponse = poleepoExceptionHandler.handleConfigurationAlreadyExistException();
        assertNotNull(configExistResponse.getBody());
        assertNull(configExistResponse.getBody().getData());

        ResponseEntity<ResponseDto<String>> genericResponse = poleepoExceptionHandler.handleGenericException();
        assertNotNull(genericResponse.getBody());
        assertNull(genericResponse.getBody().getData());
    }

    @Test
    void httpStatusCodes_ShouldBeCorrectForEachException() {
        // Verify each exception returns the correct HTTP status

        assertEquals(HttpStatus.BAD_REQUEST,
                poleepoExceptionHandler.handleValidationExceptions(methodArgumentNotValidException).getStatusCode());

        assertEquals(HttpStatus.NOT_FOUND,
                poleepoExceptionHandler.handleShopNotFoundException().getStatusCode());

        assertEquals(HttpStatus.CONFLICT,
                poleepoExceptionHandler.handleConfigurationAlreadyExistException().getStatusCode());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,
                poleepoExceptionHandler.handleGenericException().getStatusCode());
    }

    @Test
    @SuppressWarnings("unchecked")
    void responseDto_ShouldHaveCorrectStructureForAllExceptions() {
        // Test that all responses have the expected ResponseDto structure

        ResponseEntity<ResponseDto<String>>[] responses = new ResponseEntity[]{
                poleepoExceptionHandler.handleValidationExceptions(methodArgumentNotValidException),
                poleepoExceptionHandler.handleShopNotFoundException(),
                poleepoExceptionHandler.handleConfigurationAlreadyExistException(),
                poleepoExceptionHandler.handleGenericException()
        };

        for (ResponseEntity<ResponseDto<String>> response : responses) {
            assertNotNull(response);
            assertNotNull(response.getBody());

            ResponseDto<String> body = response.getBody();
            assertFalse(body.isSuccess());
            assertTrue(body.getError() > 0); // Error code should be positive
            assertNotNull(body.getMessage());
            assertFalse(body.getMessage().isEmpty());
            assertNull(body.getData());
        }
    }

    @Test
    void errorCodes_ShouldBeUniqueForEachException() {
        // Verify that each exception type returns a unique error code

        ResponseEntity<ResponseDto<String>> validationResponse = poleepoExceptionHandler.handleValidationExceptions(methodArgumentNotValidException);
        ResponseEntity<ResponseDto<String>> shopNotFoundResponse = poleepoExceptionHandler.handleShopNotFoundException();
        ResponseEntity<ResponseDto<String>> configExistResponse = poleepoExceptionHandler.handleConfigurationAlreadyExistException();
        ResponseEntity<ResponseDto<String>> genericResponse = poleepoExceptionHandler.handleGenericException();

        assertNotNull(validationResponse.getBody());
        assertNotNull(shopNotFoundResponse.getBody());
        assertNotNull(configExistResponse.getBody());
        assertNotNull(genericResponse.getBody());

        int validationErrorCode = validationResponse.getBody().getError();
        int shopNotFoundErrorCode = shopNotFoundResponse.getBody().getError();
        int configExistErrorCode = configExistResponse.getBody().getError();
        int genericErrorCode = genericResponse.getBody().getError();

        // All error codes should be different
        assertNotEquals(validationErrorCode, shopNotFoundErrorCode);
        assertNotEquals(validationErrorCode, configExistErrorCode);
        assertNotEquals(validationErrorCode, genericErrorCode);
        assertNotEquals(shopNotFoundErrorCode, configExistErrorCode);
        assertNotEquals(shopNotFoundErrorCode, genericErrorCode);
        assertNotEquals(configExistErrorCode, genericErrorCode);
    }
}

