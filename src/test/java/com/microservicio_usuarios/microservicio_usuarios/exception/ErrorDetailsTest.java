package com.microservicio_usuarios.microservicio_usuarios.exception;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

class ErrorDetailsTest {

    @Test
    void constructor_CrearErrorDetails_ConTodosLosCampos() {
        LocalDateTime timestamp = LocalDateTime.now();
        String mensaje = "Error de prueba";
        String detalles = "Detalles del error";

        ErrorDetails errorDetails = new ErrorDetails(timestamp, mensaje, detalles);

        assertNotNull(errorDetails);
        assertEquals(timestamp, errorDetails.getTimestamp());
        assertEquals(mensaje, errorDetails.getMensaje());
        assertEquals(detalles, errorDetails.getDetalles());
    }

    @Test
    void getters_RetornarValoresCorrectos() {
        LocalDateTime timestamp = LocalDateTime.of(2024, 12, 15, 10, 30);
        ErrorDetails errorDetails = new ErrorDetails(timestamp, "Test mensaje", "Test detalles");

        assertEquals(timestamp, errorDetails.getTimestamp());
        assertEquals("Test mensaje", errorDetails.getMensaje());
        assertEquals("Test detalles", errorDetails.getDetalles());
    }

    @Test
    void constructor_AceptarValoresNulos() {
        ErrorDetails errorDetails = new ErrorDetails(null, null, null);

        assertNotNull(errorDetails);
        assertNull(errorDetails.getTimestamp());
        assertNull(errorDetails.getMensaje());
        assertNull(errorDetails.getDetalles());
    }

    @Test
    void constructor_ConDiferentesMensajes() {
        ErrorDetails error1 = new ErrorDetails(LocalDateTime.now(), "Mensaje 1", "Detalles 1");
        ErrorDetails error2 = new ErrorDetails(LocalDateTime.now(), "Mensaje 2", "Detalles 2");

        assertNotEquals(error1.getMensaje(), error2.getMensaje());
        assertNotEquals(error1.getDetalles(), error2.getDetalles());
    }
}
