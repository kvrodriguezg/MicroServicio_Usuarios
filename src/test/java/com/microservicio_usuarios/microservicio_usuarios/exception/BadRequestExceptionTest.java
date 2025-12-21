package com.microservicio_usuarios.microservicio_usuarios.exception;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class BadRequestExceptionTest {

    @Test
    void constructor_CrearExcepcion_ConMensaje() {
        String mensaje = "Email ya registrado";

        BadRequestException exception = new BadRequestException(mensaje);

        assertNotNull(exception);
        assertEquals(mensaje, exception.getMessage());
    }

    @Test
    void getMessage_RetornarMensajeCorrect() {
        BadRequestException exception = new BadRequestException("Datos inválidos");

        assertEquals("Datos inválidos", exception.getMessage());
    }

    @Test
    void instanceof_RuntimeException() {
        BadRequestException exception = new BadRequestException("Test");

        assertTrue(exception instanceof RuntimeException);
    }
}
