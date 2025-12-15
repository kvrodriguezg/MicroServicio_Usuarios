package com.microservicio_usuarios.microservicio_usuarios.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.microservicio_usuarios.microservicio_usuarios.model.Usuario;
import com.microservicio_usuarios.microservicio_usuarios.service.UsuarioService;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // Obtener todos los usuarios
    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    // Obtener usuario por id
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtener(@PathVariable Long id) {
        return usuarioService.obtenerUsuarioPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear un usuario
    @PostMapping
    public ResponseEntity<Usuario> crear(@Valid @RequestBody Usuario usuario) {
        Usuario nuevo = usuarioService.crear(usuario);
        return ResponseEntity.status(201).body(nuevo);
    }

    // Actualizar un usuario
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioService.actualizar(id, usuario));
    }

    // Eliminar un usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody java.util.Map<String, String> credenciales) {
        String email = credenciales.get("email");
        String password = credenciales.get("password");
        if (email == null || password == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(usuarioService.login(email, password));
    }
}