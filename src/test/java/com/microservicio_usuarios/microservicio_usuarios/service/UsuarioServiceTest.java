package com.microservicio_usuarios.microservicio_usuarios.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.microservicio_usuarios.microservicio_usuarios.exception.BadRequestException;
import com.microservicio_usuarios.microservicio_usuarios.exception.ResourceNotFoundException;
import com.microservicio_usuarios.microservicio_usuarios.model.Usuario;
import com.microservicio_usuarios.microservicio_usuarios.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

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
    void listarTodos_RetornarListaDeUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario));

        List<Usuario> resultados = usuarioService.listarTodos();

        assertNotNull(resultados);
        assertEquals(1, resultados.size());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    @SuppressWarnings("null")
    void crear_GuardarUsuario_CuandoEmailNoExiste() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        doReturn(usuario).when(usuarioRepository).save(any(Usuario.class));

        Usuario resultado = usuarioService.crear(usuario);

        assertNotNull(resultado);
        assertEquals("test@example.com", resultado.getEmail());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @SuppressWarnings("null")
    void crear_LanzarExcepcion_CuandoEmailExiste() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> usuarioService.crear(usuario));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void obtenerUsuarioPorId_RetornarUsuario_CuandoExiste() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.obtenerUsuarioPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
    }

    @Test
    void obtenerUsuarioPorId_RetornarVacio_CuandoNoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioService.obtenerUsuarioPorId(99L);

        assertFalse(resultado.isPresent());
    }

    @Test
    @SuppressWarnings("null")
    void actualizar_ActualizarUsuario_CuandoExisteYEmailValido() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        doReturn(usuario).when(usuarioRepository).save(any(Usuario.class));

        Usuario updateDto = new Usuario();
        updateDto.setNombre("Updated Name");
        updateDto.setEmail("test@example.com"); 
        updateDto.setRol("ADMIN");

        Usuario resultado = usuarioService.actualizar(1L, updateDto);

        assertNotNull(resultado);
        assertEquals("Updated Name", resultado.getNombre()); 
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void actualizar_LanzarExcepcion_CuandoUsuarioNoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        Usuario updateDto = new Usuario();
        assertThrows(ResourceNotFoundException.class, () -> usuarioService.actualizar(99L, updateDto));
    }

    @Test
    void actualizar_LanzarExcepcion_CuandoEmailPerteneceAOtroUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByEmail("other@example.com")).thenReturn(true);

        Usuario updateDto = new Usuario();
        updateDto.setEmail("other@example.com");

        assertThrows(BadRequestException.class, () -> usuarioService.actualizar(1L, updateDto));
    }

    @Test
    @SuppressWarnings("null")
    void eliminar_EliminarUsuario_CuandoExiste() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        usuarioService.eliminar(1L);

        verify(usuarioRepository, times(1)).delete(usuario);
    }

    @SuppressWarnings("null")
    @Test
    void actualizar_ActualizarEmail_CuandoNuevoEmailNoExiste() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByEmail("new@example.com")).thenReturn(false);
        doReturn(usuario).when(usuarioRepository).save(any(Usuario.class));

        Usuario updateDto = new Usuario();
        updateDto.setEmail("new@example.com");

        Usuario resultado = usuarioService.actualizar(1L, updateDto);

        assertNotNull(resultado);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @SuppressWarnings("null")
    @Test
    void actualizar_ActualizarPassword_CuandoEsValida() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("newpass")).thenReturn("encodedNewPass");
        doReturn(usuario).when(usuarioRepository).save(any(Usuario.class));

        Usuario updateDto = new Usuario();
        updateDto.setEmail("test@example.com");
        updateDto.setPassword("newpass");

        Usuario resultado = usuarioService.actualizar(1L, updateDto);

        assertNotNull(resultado);
        verify(passwordEncoder, times(1)).encode("newpass");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void login_RetornarUsuario_CuandoCredencialesCorrectas() {
        usuario.setPassword("encodedPass");
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("password123", "encodedPass")).thenReturn(true);

        Usuario resultado = usuarioService.login("test@example.com", "password123");

        assertNotNull(resultado);
        assertEquals("test@example.com", resultado.getEmail());
    }

    @Test
    void login_LanzarExcepcion_CuandoUsuarioNoExiste() {
        when(usuarioRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.login("nonexistent@example.com", "pass"));
    }

    @Test
    void login_LanzarExcepcion_CuandoPasswordIncorrecta() {
        usuario.setPassword("encodedPass");
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("wrongpass", "encodedPass")).thenReturn(false);

        assertThrows(BadRequestException.class, () -> usuarioService.login("test@example.com", "wrongpass"));
    }

    @Test
    void crear_LanzarExcepcion_CuandoPasswordEsNulo() {
        usuario.setPassword(null);
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);

        assertThrows(BadRequestException.class, () -> usuarioService.crear(usuario));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void crear_LanzarExcepcion_CuandoPasswordEsVacio() {
        usuario.setPassword("   ");
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);

        assertThrows(BadRequestException.class, () -> usuarioService.crear(usuario));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @SuppressWarnings("null")
    void crear_LanzarExcepcion_CuandoRutYaExiste() {
        usuario.setRut("12345678-9");
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository.existsByRut("123456789")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> usuarioService.crear(usuario));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @SuppressWarnings("null")
    void crear_LanzarExcepcion_CuandoTelefonoYaExiste() {
        usuario.setTelefono("987654321");
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository.existsByTelefono("987654321")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> usuarioService.crear(usuario));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @SuppressWarnings("null")
    void crear_LimpiarRut_CuandoContieneFormatoConPuntosYGuiones() {
        usuario.setRut("12.345.678-9");
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository.existsByRut("123456789")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        doReturn(usuario).when(usuarioRepository).save(any(Usuario.class));

        Usuario resultado = usuarioService.crear(usuario);

        assertNotNull(resultado);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @SuppressWarnings("null")
    void crear_ManejarRutNulo() {
        usuario.setRut(null);
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        doReturn(usuario).when(usuarioRepository).save(any(Usuario.class));

        Usuario resultado = usuarioService.crear(usuario);

        assertNotNull(resultado);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @SuppressWarnings("null")
    void actualizar_LanzarExcepcion_CuandoRutPerteneceAOtroUsuario() {
        usuario.setRut("111111111");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Usuario updateDto = new Usuario();
        updateDto.setEmail("test@example.com");
        updateDto.setRut("22.222.222-2");
        when(usuarioRepository.existsByRut("222222222")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> usuarioService.actualizar(1L, updateDto));
    }

    @Test
    @SuppressWarnings("null")
    void actualizar_LanzarExcepcion_CuandoTelefonoPerteneceAOtroUsuario() {
        usuario.setTelefono("111111111");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Usuario updateDto = new Usuario();
        updateDto.setEmail("test@example.com");
        updateDto.setTelefono("222222222");
        when(usuarioRepository.existsByTelefono("222222222")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> usuarioService.actualizar(1L, updateDto));
    }

    @Test
    @SuppressWarnings("null")
    void actualizar_NoActualizarPassword_CuandoEsVacio() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        doReturn(usuario).when(usuarioRepository).save(any(Usuario.class));

        Usuario updateDto = new Usuario();
        updateDto.setEmail("test@example.com");
        updateDto.setPassword("");

        Usuario resultado = usuarioService.actualizar(1L, updateDto);

        assertNotNull(resultado);
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @SuppressWarnings("null")
    void actualizar_NoActualizarPassword_CuandoEsNulo() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        doReturn(usuario).when(usuarioRepository).save(any(Usuario.class));

        Usuario updateDto = new Usuario();
        updateDto.setEmail("test@example.com");
        updateDto.setPassword(null);

        Usuario resultado = usuarioService.actualizar(1L, updateDto);

        assertNotNull(resultado);
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void eliminar_LanzarExcepcion_CuandoUsuarioNoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.eliminar(99L));
        verify(usuarioRepository, never()).delete(any(Usuario.class));
    }

    @Test
    @SuppressWarnings("null")
    void actualizar_ActualizarRut_CuandoNuevoRutNoExiste() {
        usuario.setRut("111111111");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByRut("222222222")).thenReturn(false);
        doReturn(usuario).when(usuarioRepository).save(any(Usuario.class));

        Usuario updateDto = new Usuario();
        updateDto.setEmail("test@example.com");
        updateDto.setRut("22.222.222-2");

        Usuario resultado = usuarioService.actualizar(1L, updateDto);

        assertNotNull(resultado);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @SuppressWarnings("null")
    void actualizar_ActualizarTelefono_CuandoNuevoTelefonoNoExiste() {
        usuario.setTelefono("111111111");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByTelefono("222222222")).thenReturn(false);
        doReturn(usuario).when(usuarioRepository).save(any(Usuario.class));

        Usuario updateDto = new Usuario();
        updateDto.setEmail("test@example.com");
        updateDto.setTelefono("222222222");

        Usuario resultado = usuarioService.actualizar(1L, updateDto);

        assertNotNull(resultado);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @SuppressWarnings("null")
    void actualizar_NoValidarRut_CuandoRutNoHaCambiado() {
        usuario.setRut("111111111");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        doReturn(usuario).when(usuarioRepository).save(any(Usuario.class));

        Usuario updateDto = new Usuario();
        updateDto.setEmail("test@example.com");
        updateDto.setRut("11.111.111-1");

        Usuario resultado = usuarioService.actualizar(1L, updateDto);

        assertNotNull(resultado);
        verify(usuarioRepository, never()).existsByRut(anyString());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @SuppressWarnings("null")
    void actualizar_NoValidarTelefono_CuandoTelefonoNoHaCambiado() {
        usuario.setTelefono("111111111");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        doReturn(usuario).when(usuarioRepository).save(any(Usuario.class));

        Usuario updateDto = new Usuario();
        updateDto.setEmail("test@example.com");
        updateDto.setTelefono("111111111");

        Usuario resultado = usuarioService.actualizar(1L, updateDto);

        assertNotNull(resultado);
        verify(usuarioRepository, never()).existsByTelefono(anyString());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }
}
