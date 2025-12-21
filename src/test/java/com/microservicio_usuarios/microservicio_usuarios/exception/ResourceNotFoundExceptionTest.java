package com.microservicio_usuarios.microservicio_usuarios.exception;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class ResourceNotFoundExceptionTest {

    @Test
    void constructor_CrearExcepcion_ConMensaje() {
        String mensaje = "Usuario no encontrado";

        ResourceNotFoundException exception = new ResourceNotFoundException(mensaje);

        assertNotNull(exception);
        assertEquals(mensaje, exception.getMessage());
    }

    @Test
    void getMessage_RetornarMensajeCorrect() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Recurso no existe");

        assertEquals("Recurso no existe", exception.getMessage());
    }

    @Test
    void instanceof_RuntimeException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Test");

        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void lanzarExcepcion_CapturarEnCatch() {
        assertThrows(ResourceNotFoundException.class, () -> {
            throw new ResourceNotFoundException("Error capturado");
        });
    }
}
