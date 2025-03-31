package org.com.dianping.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleRuntimeException_ShouldReturnBadRequest_WithErrorMessage() {
        String errorMessage = "Test error message";
        RuntimeException exception = new RuntimeException(errorMessage);

        ResponseEntity<?> responseEntity = globalExceptionHandler.handleRuntimeException(exception);

        assertNotNull(responseEntity);
        assertEquals(400, responseEntity.getStatusCodeValue());

        Object body = responseEntity.getBody();
        assertNotNull(body);
        assertTrue(body instanceof GlobalExceptionHandler.ErrorResponse);

        GlobalExceptionHandler.ErrorResponse errorResponse = (GlobalExceptionHandler.ErrorResponse) body;
        assertEquals(errorMessage, errorResponse.message());
    }
}
