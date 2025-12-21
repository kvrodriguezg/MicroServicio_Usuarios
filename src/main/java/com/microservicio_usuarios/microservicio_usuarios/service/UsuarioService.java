package com.microservicio_usuarios.microservicio_usuarios.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.microservicio_usuarios.microservicio_usuarios.exception.BadRequestException;
import com.microservicio_usuarios.microservicio_usuarios.exception.ResourceNotFoundException;
import com.microservicio_usuarios.microservicio_usuarios.model.Usuario;
import com.microservicio_usuarios.microservicio_usuarios.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Objects;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Usuario> listarTodos() {
        log.info("Listando todos los usuarios");
        return usuarioRepository.findAll();
    }

    private String limpiarRut(String rut) {
        if (rut == null)
            return null;
        return rut.replace(".", "").replace("-", "").toUpperCase();
    }

    public Usuario crear(Usuario usuario) {
        log.info("Creando usuario: {}", usuario.getEmail());

        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            log.warn("Intento crear usuario con email ya existente: {}", usuario.getEmail());
            throw new BadRequestException("El email ya está registrado");
        }

        String rutLimpio = limpiarRut(usuario.getRut());
        usuario.setRut(rutLimpio);

        if (usuario.getRut() != null && usuarioRepository.existsByRut(usuario.getRut())) {
            log.warn("Intento crear usuario con rut ya existente: {}", usuario.getRut());
            throw new BadRequestException("El rut ya está registrado");
        }

        if (usuario.getTelefono() != null && usuarioRepository.existsByTelefono(usuario.getTelefono())) {
            log.warn("Intento crear usuario con telefono ya existente: {}", usuario.getTelefono());
            throw new BadRequestException("El teléfono ya está registrado");
        }

        if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
            throw new BadRequestException("La contraseña es obligatoria");
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setFechaRegistro(LocalDateTime.now());
        Usuario saved = usuarioRepository.save(usuario);

        Long id = Objects.requireNonNull(saved.getId(), "El id no debe ser nulo después de guardar");
        log.info("Usuario creado con id {}", id);

        return saved;
    }

    @SuppressWarnings("null")
    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        log.info("Buscando usuario con id {}", id);
        return usuarioRepository.findById(id);
    }

    @SuppressWarnings("null")
    private Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    public Usuario actualizar(Long id, Usuario dto) {
        log.info("Actualizando usuario con id {}", id);

        Usuario existente = obtenerPorId(id);

        if (!existente.getEmail().equals(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
            log.warn("Intento de actualizar con un email ya existente: {}", dto.getEmail());
            throw new BadRequestException("El email ya está registrado por otro usuario");
        }

        String rutLimpio = limpiarRut(dto.getRut());
        dto.setRut(rutLimpio);

        if (dto.getRut() != null && !dto.getRut().equals(existente.getRut())
                && usuarioRepository.existsByRut(dto.getRut())) {
            log.warn("Intento de actualizar con un rut ya existente: {}", dto.getRut());
            throw new BadRequestException("El rut ya está registrado por otro usuario");
        }

        if (dto.getTelefono() != null && !dto.getTelefono().equals(existente.getTelefono())
                && usuarioRepository.existsByTelefono(dto.getTelefono())) {
            log.warn("Intento de actualizar con un telefono ya existente: {}", dto.getTelefono());
            throw new BadRequestException("El teléfono ya está registrado por otro usuario");
        }

        existente.setNombre(dto.getNombre());
        existente.setEmail(dto.getEmail());
        existente.setRol(dto.getRol());

        existente.setRut(dto.getRut());
        existente.setTelefono(dto.getTelefono());
        existente.setFechaNacimiento(dto.getFechaNacimiento());

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            existente.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        Usuario updated = usuarioRepository.save(existente);

        Long updatedId = Objects.requireNonNull(updated.getId(), "El id no debe ser nulo después de actualizar");
        log.info("Usuario actualizado id {}", updatedId);

        return updated;
    }

    @SuppressWarnings("null")
    public void eliminar(Long id) {
        log.info("Eliminando usuario con id {}", id);
        Usuario existente = obtenerPorId(id);
        usuarioRepository.delete(existente);
        log.info("Usuario eliminado id {}", id);
    }

    public Usuario login(String email, String password) {
        log.info("Intento de login para email: {}", email);
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new BadRequestException("Credenciales incorrectas");
        }
        return usuario;
    }
}