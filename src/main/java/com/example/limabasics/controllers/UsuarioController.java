package com.example.limabasics.controllers;

import com.example.limabasics.entities.Usuario;
import com.example.limabasics.services.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final CarritoController carritoController;

    public UsuarioController(UsuarioService usuarioService, CarritoController carritoController) {
        this.usuarioService = usuarioService;
        this.carritoController = carritoController;
    }

    @PostMapping("/login-ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> procesarLoginAjax(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorEmail(email);

        if (usuarioOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "El correo '" + email + "' no está registrado en Lima Basics.");
            return ResponseEntity.ok(response);
        }

        Usuario usuario = usuarioOpt.get();
        if (!usuario.getPassword().equals(password)) {
            response.put("success", false);
            response.put("message", "La contraseña ingresada es incorrecta. Verifica e inténtalo nuevamente.");
            return ResponseEntity.ok(response);
        }

        session.setAttribute("usuarioId", usuario.getId());
        session.setAttribute("usuarioNombre", usuario.getNombre());
        session.setAttribute("usuarioEmail", usuario.getEmail());
        session.setAttribute("usuarioRol", usuario.getRol());

        carritoController.sincronizarContadoresSesion(session);

        response.put("success", true);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/registro-ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> procesarRegistroAjax(
            @RequestParam("nombre") String nombre,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        if (usuarioService.buscarPorEmail(email).isPresent()) {
            response.put("success", false);
            response.put("message", "Este correo electrónico ya está registrado. Intenta iniciar sesión.");
            return ResponseEntity.ok(response);
        }

        Usuario nuevo = usuarioService.registrarUsuario(nombre, email, password);
        session.setAttribute("usuarioId", nuevo.getId());
        session.setAttribute("usuarioNombre", nuevo.getNombre());
        session.setAttribute("usuarioEmail", nuevo.getEmail());
        session.setAttribute("usuarioRol", nuevo.getRol());

        carritoController.sincronizarContadoresSesion(session);

        response.put("success", true);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}