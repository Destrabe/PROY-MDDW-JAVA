package com.example.limabasics.services;

import com.example.limabasics.entities.Usuario;
import com.example.limabasics.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        if (email == null) return Optional.empty();
        return usuarioRepository.findByEmailIgnoreCase(email.trim());
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Usuario registrarUsuario(String nombre, String email, String password) {
        Usuario nuevoUsuario = new Usuario(nombre.trim(), email.trim().toLowerCase(), password, "USER");
        return usuarioRepository.save(nuevoUsuario);
    }
}