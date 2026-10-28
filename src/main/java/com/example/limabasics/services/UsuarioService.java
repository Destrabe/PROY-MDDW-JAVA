package com.example.limabasics.services;

import com.example.limabasics.entities.Usuario;
import java.util.Optional;

public interface UsuarioService {
    Optional<Usuario> buscarPorEmail(String email);

    Optional<Usuario> buscarPorId(Long id);

    Usuario registrarUsuario(String nombre, String email, String password);
}