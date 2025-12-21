package com.microservicio_usuarios.microservicio_usuarios.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;

class UsuarioTest {

    @Test
    void constructor_CrearUsuarioVacio() {
        Usuario usuario = new Usuario();

        assertNotNull(usuario);
        assertNull(usuario.getId());
    }

    @Test
    void constructorConTodosLosArguments_CrearUsuario() {
        LocalDateTime fechaRegistro = LocalDateTime.now();
        LocalDate fechaNacimiento = LocalDate.of(1990, 1, 1);

        Usuario usuario = new Usuario(
                1L, "Juan Perez", "juan@example.com", "USER",
                "password123", fechaRegistro, "12345678-9",
                "987654321", fechaNacimiento);

        assertNotNull(usuario);
        assertEquals(1L, usuario.getId());
        assertEquals("Juan Perez", usuario.getNombre());
        assertEquals("juan@example.com", usuario.getEmail());
        assertEquals("USER", usuario.getRol());
        assertEquals("password123", usuario.getPassword());
        assertEquals(fechaRegistro, usuario.getFechaRegistro());
        assertEquals("12345678-9", usuario.getRut());
        assertEquals("987654321", usuario.getTelefono());
        assertEquals(fechaNacimiento, usuario.getFechaNacimiento());
    }

    @Test
    void builder_CrearUsuario_ConBuilder() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nombre("Test User")
                .email("test@example.com")
                .rol("ADMIN")
                .password("pass123")
                .build();

        assertNotNull(usuario);
        assertEquals(1L, usuario.getId());
        assertEquals("Test User", usuario.getNombre());
        assertEquals("test@example.com", usuario.getEmail());
        assertEquals("ADMIN", usuario.getRol());
        assertEquals("pass123", usuario.getPassword());
    }

    @Test
    void setters_ModificarValores_Correctamente() {
        Usuario usuario = new Usuario();
        LocalDateTime fechaRegistro = LocalDateTime.now();
        LocalDate fechaNacimiento = LocalDate.of(1995, 5, 15);

        usuario.setId(10L);
        usuario.setNombre("Maria Lopez");
        usuario.setEmail("maria@example.com");
        usuario.setRol("ADMIN");
        usuario.setPassword("newpass");
        usuario.setFechaRegistro(fechaRegistro);
        usuario.setRut("98765432-1");
        usuario.setTelefono("123456789");
        usuario.setFechaNacimiento(fechaNacimiento);

        assertEquals(10L, usuario.getId());
        assertEquals("Maria Lopez", usuario.getNombre());
        assertEquals("maria@example.com", usuario.getEmail());
        assertEquals("ADMIN", usuario.getRol());
        assertEquals("newpass", usuario.getPassword());
        assertEquals(fechaRegistro, usuario.getFechaRegistro());
        assertEquals("98765432-1", usuario.getRut());
        assertEquals("123456789", usuario.getTelefono());
        assertEquals(fechaNacimiento, usuario.getFechaNacimiento());
    }

    @Test
    void equals_RetornarTrue_CuandoUsuariosSonIguales() {
        Usuario usuario1 = Usuario.builder()
                .id(1L)
                .nombre("Test")
                .email("test@example.com")
                .rol("USER")
                .password("pass")
                .build();

        Usuario usuario2 = Usuario.builder()
                .id(1L)
                .nombre("Test")
                .email("test@example.com")
                .rol("USER")
                .password("pass")
                .build();

        assertEquals(usuario1, usuario2);
    }

    @Test
    void hashCode_RetornarMismoHash_ParaUsuariosIguales() {
        Usuario usuario1 = Usuario.builder()
                .id(1L)
                .nombre("Test")
                .email("test@example.com")
                .build();

        Usuario usuario2 = Usuario.builder()
                .id(1L)
                .nombre("Test")
                .email("test@example.com")
                .build();

        assertEquals(usuario1.hashCode(), usuario2.hashCode());
    }

    @Test
    void toString_RetornarRepresentacionString() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Test");
        usuario.setEmail("test@example.com");

        String resultado = usuario.toString();

        assertNotNull(resultado);
        assertTrue(resultado.contains("Usuario"));
    }
}
