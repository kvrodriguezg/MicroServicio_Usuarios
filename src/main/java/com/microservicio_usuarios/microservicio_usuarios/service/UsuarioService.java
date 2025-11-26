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

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    //Obtener todos los usuarios
    public List<Usuario> listarTodos() {
        log.info("Listando todos los usuarios");
        return usuarioRepository.findAll();
    }

    //Crear un usuario
    public Usuario crear(Usuario usuario) {
        log.info("Creando usuario: {}", usuario.getEmail());

        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            log.warn("Intento crear usuario con email ya existente: {}", usuario.getEmail());
            throw new BadRequestException("El email ya está registrado");
        }

        usuario.setFechaRegistro(LocalDateTime.now());
        Usuario saved = usuarioRepository.save(usuario);

        //Evita advertencia de tipo nulo
        Long id = Objects.requireNonNull(saved.getId(), "El id no debe ser nulo después de guardar");
        log.info("Usuario creado con id {}", id);

        return saved;
    }

    //Obtener usuario mediante id
    @SuppressWarnings("null")
    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        log.info("Buscando usuario con id {}", id);
        return usuarioRepository.findById(id);
    }

    //Obtener usuario mediante con excepción
    @SuppressWarnings("null")
    private Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    //Actualizar un usuario
    public Usuario actualizar(Long id, Usuario dto) {
        log.info("Actualizando usuario con id {}", id);

        Usuario existente = obtenerPorId(id);

        if (!existente.getEmail().equals(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
            log.warn("Intento de actualizar con un email ya existente: {}", dto.getEmail());
            throw new BadRequestException("El email ya está registrado por otro usuario");
        }

        existente.setNombre(dto.getNombre());
        existente.setEmail(dto.getEmail());
        existente.setRol(dto.getRol());

        Usuario updated = usuarioRepository.save(existente);

        //Evita advertencia de tipo nulo
        Long updatedId = Objects.requireNonNull(updated.getId(), "El id no debe ser nulo después de actualizar");
        log.info("Usuario actualizado id {}", updatedId);

        return updated;
    }

    //Eliminar usuario
    @SuppressWarnings("null")
    public void eliminar(Long id) {
        log.info("Eliminando usuario con id {}", id);
        Usuario existente = obtenerPorId(id);
        usuarioRepository.delete(existente);
        log.info("Usuario eliminado id {}", id);
    }
}