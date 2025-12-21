package com.microservicio_usuarios.microservicio_usuarios.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleNotFound(ResourceNotFoundException ex) {
        ErrorDetails error = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), "Recurso no encontrado");
        log.warn("ResourceNotFound: {}", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorDetails> handleBadRequest(BadRequestException ex) {
        ErrorDetails error = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), "Solicitud inválida");
        log.warn("BadRequest: {}", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errores = ex.getBindingResult()
                .getFieldErrors().stream()
                .map(FieldError::getDefaultMessage).collect(Collectors.toList());
        ErrorDetails error = new ErrorDetails(LocalDateTime.now(), "Validación fallida", errores.toString());
        log.warn("Validation errors: {}", errores);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobal(Exception ex) {
        ErrorDetails error = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), "Error interno del servidor");
        log.error("Error interno: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}