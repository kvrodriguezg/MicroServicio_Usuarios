package com.microservicio_usuarios.microservicio_usuarios.exception;

import java.time.LocalDateTime;

public class ErrorDetails {
    private LocalDateTime timestamp;
    private String mensaje;
    private String detalles;

    public ErrorDetails(LocalDateTime timestamp, String mensaje, String detalles) {
        this.timestamp = timestamp;
        this.mensaje = mensaje;
        this.detalles = detalles;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getDetalles() {
        return detalles;
    }
}