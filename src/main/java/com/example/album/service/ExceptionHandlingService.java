package com.example.album.service;

import com.example.album.common.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ExceptionHandlingService {

    /**
     * Handle exceptions and create appropriate ResponseEntity
     * @param e The exception to handle
     * @param defaultMessage Default message to use for non-business exceptions
     * @return ResponseEntity with appropriate status and body
     */
    public ResponseEntity<?> handleException(Exception e, String defaultMessage) {
        if (e instanceof BusinessException) {
            BusinessException be = (BusinessException) e;
            return ResponseEntity.status(be.getCode())
                    .body(be.getMessage());
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(defaultMessage + ": " + e.getMessage());
        }
    }

    /**
     * Handle exceptions and create appropriate ResponseEntity with structured response
     * @param e The exception to handle
     * @param defaultMessage Default message to use for non-business exceptions
     * @return ResponseEntity with appropriate status and structured body
     */
    public ResponseEntity<?> handleExceptionWithStructuredResponse(Exception e, String defaultMessage) {
        Map<String, Object> response = new HashMap<>();

        if (e instanceof BusinessException) {
            BusinessException be = (BusinessException) e;
            response.put("message", be.getMessage());
            return ResponseEntity.status(be.getCode()).body(response);
        } else {
            response.put("message", defaultMessage + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}