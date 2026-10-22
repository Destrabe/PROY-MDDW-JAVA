package com.example.limabasics.controllers;

import com.example.limabasics.entities.Usuario;
import com.example.limabasics.services.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registro")
    public String procesarRegistro(
            @RequestParam("nombre") String nombre,
            @RequestParam("email") String email,
            @RequestParam("password") String password) {
        
        usuarioService.registrarUsuario(nombre, email, password);
        return "redirect:/"; 
    }

    @PostMapping("/login")
    public String procesarLogin(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session) {
        
        Usuario usuario = usuarioService.validarLogin(email, password);
        
        if (usuario != null) {
            session.setAttribute("usuarioId", usuario.getId());
            session.setAttribute("usuarioNombre", usuario.getNombre());
            session.setAttribute("usuarioRol", usuario.getRol());
            return "redirect:/"; 
        } else {
            return "redirect:/?error=true";
        }
    }
    
    @PostMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate(); 
        return "redirect:/";
    }
}