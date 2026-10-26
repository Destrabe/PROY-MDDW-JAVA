package com.example.limabasics.dto;

import com.example.limabasics.entities.Usuario;

public record UsuarioDTO(Long id, String nombre, String email, String rol) {
    
    public static UsuarioDTO desdeEntidad(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol()
        );
    }
}