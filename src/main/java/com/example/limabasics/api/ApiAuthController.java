package com.example.limabasics.api;

import com.example.limabasics.dto.UsuarioDTO; 
import com.example.limabasics.entities.Usuario;
import com.example.limabasics.services.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiAuthController {

    private final UsuarioService usuarioService;

    public ApiAuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginApi(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorEmail(email);

        if (usuarioOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "El correo '" + email + "' no está registrado.");
            return ResponseEntity.status(401).body(response);
        }

        Usuario usuario = usuarioOpt.get();
        if (!usuario.getPassword().equals(password)) {
            response.put("success", false);
            response.put("message", "La contraseña ingresada es incorrecta.");
            return ResponseEntity.status(401).body(response);
        }

        session.setAttribute("usuarioId", usuario.getId());
        session.setAttribute("usuarioNombre", usuario.getNombre());
        session.setAttribute("usuarioEmail", usuario.getEmail());
        session.setAttribute("usuarioRol", usuario.getRol());

        response.put("success", true);
        response.put("message", "¡Inicio de sesión exitoso!");
        response.put("nombre", usuario.getNombre());
        response.put("rol", usuario.getRol());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/perfil/{id}")
    public ResponseEntity<UsuarioDTO> obtenerPerfil(@PathVariable Long id) {
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(id);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuarioReal = usuarioOpt.get();
            UsuarioDTO usuarioSeguro = UsuarioDTO.desdeEntidad(usuarioReal);
            return ResponseEntity.ok(usuarioSeguro);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}