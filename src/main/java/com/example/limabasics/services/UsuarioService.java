package com.example.limabasics.services;

import com.example.limabasics.entities.Usuario;
import com.example.limabasics.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email.trim());
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario registrarUsuario(String nombre, String email, String password) {
        Usuario nuevoUsuario = new Usuario(nombre.trim(), email.trim(), password, "USER");
        return usuarioRepository.save(nuevoUsuario);
    }
}