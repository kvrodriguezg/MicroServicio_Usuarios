package com.microservicio_usuarios.microservicio_usuarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.microservicio_usuarios.microservicio_usuarios.model.Usuario;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByRut(String rut);

    boolean existsByTelefono(String telefono);
}