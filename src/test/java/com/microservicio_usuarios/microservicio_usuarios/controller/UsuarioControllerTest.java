package com.microservicio_usuarios.microservicio_usuarios.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicio_usuarios.microservicio_usuarios.model.Usuario;
import com.microservicio_usuarios.microservicio_usuarios.service.UsuarioService;

@WebMvcTest(UsuarioController.class)
@org.springframework.context.annotation.Import(com.microservicio_usuarios.microservicio_usuarios.config.SecurityConfig.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    private Usuario usuario;

    // Antes de cada test
    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Test User");
        usuario.setEmail("test@example.com");
        usuario.setRol("USER");
        usuario.setPassword("password123");
    }

    @Test
    void listar_RetornarListaDeUsuarios() throws Exception {
        when(usuarioService.listarTodos()).thenReturn(Arrays.asList(usuario));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("test@example.com"));
    }

    @Test
    void obtener_RetornarUsuario_CuandoExiste() throws Exception {
        when(usuarioService.obtenerUsuarioPorId(1L)).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Test User"));
    }

    @Test
    void obtener_RetornarNotFound_CuandoNoExiste() throws Exception {
        when(usuarioService.obtenerUsuarioPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/usuarios/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @SuppressWarnings("null")
    void crear_RetornarCreated_CuandoEsValido() throws Exception {
        when(usuarioService.crear(any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @SuppressWarnings("null")
    void actualizar_RetornarOk_CuandoActualizacionExitosa() throws Exception {
        when(usuarioService.actualizar(eq(1L), any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(put("/api/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Test User"));
    }

    @Test
    void eliminar_RetornarNoContent() throws Exception {
        doNothing().when(usuarioService).eliminar(1L);

        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @SuppressWarnings("null")
    void crear_RetornarBadRequest_CuandoDatosInvalidos() throws Exception {
        Usuario usuarioInvalido = new Usuario(); // Campos vacíos

        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SuppressWarnings("null")
    void crear_RetornarBadRequest_CuandoLanzaExcepcion() throws Exception {
        when(usuarioService.crear(any(Usuario.class)))
                .thenThrow(new com.microservicio_usuarios.microservicio_usuarios.exception.BadRequestException(
                        "Error simple"));

        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listar_RetornarInternalServer_CuandoFalla() throws Exception {
        when(usuarioService.listarTodos()).thenThrow(new RuntimeException("Error inesperado"));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @SuppressWarnings("null")
    void actualizar_RetornarNotFound_CuandoUsuarioNoExiste() throws Exception {
        when(usuarioService.actualizar(eq(99L), any(Usuario.class)))
                .thenThrow(new com.microservicio_usuarios.microservicio_usuarios.exception.ResourceNotFoundException(
                        "Usuario no encontrado"));

        mockMvc.perform(put("/api/usuarios/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isNotFound());
    }

    @Test
    void login_RetornarOk_CuandoCredencialesCorrectas() throws Exception {
        when(usuarioService.login("test@example.com", "password123")).thenReturn(usuario);

        java.util.Map<String, String> creds = new java.util.HashMap<>();
        creds.put("email", "test@example.com");
        creds.put("password", "password123");

        mockMvc.perform(post("/api/usuarios/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(creds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void login_RetornarBadRequest_CuandoFaltanDatos() throws Exception {
        java.util.Map<String, String> creds = new java.util.HashMap<>();
        creds.put("email", "test@example.com");
        // missing password

        mockMvc.perform(post("/api/usuarios/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(creds)))
                .andExpect(status().isBadRequest());
    }
}
