package com.microservicio_usuarios.microservicio_usuarios.exception;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.List;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_RetornarNotFound_CuandoResourceNotFoundExceptionLanzada() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Usuario no encontrado");

        ResponseEntity<ErrorDetails> response = handler.handleNotFound(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorDetails body = response.getBody();
        assertNotNull(body);
        assertEquals("Usuario no encontrado", body.getMensaje());
        assertEquals("Recurso no encontrado", body.getDetalles());
    }

    @Test
    void handleBadRequest_RetornarBadRequest_CuandoBadRequestExceptionLanzada() {
        BadRequestException exception = new BadRequestException("Email ya existe");

        ResponseEntity<ErrorDetails> response = handler.handleBadRequest(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorDetails body = response.getBody();
        assertNotNull(body);
        assertEquals("Email ya existe", body.getMensaje());
        assertEquals("Solicitud inválida", body.getDetalles());
    }

    @Test
    void handleValidationExceptions_RetornarBadRequest_CuandoValidacionFalla() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError1 = new FieldError("usuario", "email", "Email es obligatorio");
        FieldError fieldError2 = new FieldError("usuario", "nombre", "Nombre es obligatorio");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        ResponseEntity<ErrorDetails> response = handler.handleValidationExceptions(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorDetails body = response.getBody();
        assertNotNull(body);
        assertEquals("Validación fallida", body.getMensaje());
        assertTrue(body.getDetalles().contains("Email es obligatorio"));
        assertTrue(body.getDetalles().contains("Nombre es obligatorio"));
    }

    @Test
    void handleGlobal_RetornarInternalServerError_CuandoExcepcionGenericaLanzada() {
        Exception exception = new RuntimeException("Error inesperado");

        ResponseEntity<ErrorDetails> response = handler.handleGlobal(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorDetails body = response.getBody();
        assertNotNull(body);
        assertEquals("Error inesperado", body.getMensaje());
        assertEquals("Error interno del servidor", body.getDetalles());
    }
}
