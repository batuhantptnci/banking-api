package com.batuhan.bankingapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.batuhan.bankingapi.exception.InvalidTransferException;
import com.batuhan.bankingapi.exception.AccountNotFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFound(
            UserNotFoundException exception
    ) {
        return ResponseEntity
                .status(404)
                .body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleEmailAlreadyExists(
            EmailAlreadyExistsException exception
    ) {
        return ResponseEntity
                .status(409)
                .body(Map.of("message", exception.getMessage()));
    }
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleAccountNotFound(
            AccountNotFoundException exception
    ) {
        return ResponseEntity
                .status(404)
                .body(Map.of("message", exception.getMessage()));
    }
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<Map<String, String>> handleInsufficientBalance(
            InsufficientBalanceException exception
    ) {
        return ResponseEntity
                .badRequest()
                .body(Map.of("message", exception.getMessage()));
    }
    @ExceptionHandler(InvalidTransferException.class)
    public ResponseEntity<Map<String, String>> handleInvalidTransfer(
            InvalidTransferException exception
    ) {
        return ResponseEntity
                .badRequest()
                .body(Map.of("message", exception.getMessage()));
    }
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCredentials(
            InvalidCredentialsException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", exception.getMessage()));
    }
    @ExceptionHandler(AccountAccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccountAccessDenied(
            AccountAccessDeniedException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", exception.getMessage()));
    }
}