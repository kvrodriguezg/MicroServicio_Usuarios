package com.microservicio_usuarios.microservicio_usuarios.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.microservicio_usuarios.microservicio_usuarios.model.Usuario;
import java.util.Optional;

//Repositorio
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
}