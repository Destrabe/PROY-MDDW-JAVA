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

    public Usuario registrarUsuario(String nombre, String email, String password) {
        Usuario nuevoUsuario = new Usuario(nombre, email, password, "USER");
        return usuarioRepository.save(nuevoUsuario);
    }

    public Usuario validarLogin(String email, String password) {
        Optional<Usuario> usuarioEncontrado = usuarioRepository.findByEmail(email);
        if (usuarioEncontrado.isPresent() && usuarioEncontrado.get().getPassword().equals(password)) {
            return usuarioEncontrado.get(); 
        }
        return null;
    }
}