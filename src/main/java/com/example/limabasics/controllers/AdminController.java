package com.example.limabasics.controllers;

import com.example.limabasics.entities.Producto;
import com.example.limabasics.entities.Usuario;
import com.example.limabasics.repositories.ProductoRepository;
import com.example.limabasics.repositories.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public AdminController(ProductoRepository productoRepository, UsuarioRepository usuarioRepository) {
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private boolean esAdmin(HttpSession session) {
        return "ADMIN".equals(session.getAttribute("usuarioRol"));
    }

    private String obtenerClaseEtiqueta(String etiqueta) {
        if (etiqueta == null || etiqueta.isBlank()) return null;
        return switch (etiqueta.trim()) {
            case "Nuevo" -> "bg-[#10b981]";
            case "Más vendido" -> "bg-blue-600";
            case "Últimas unidades" -> "bg-[#1f2937]";
            default -> "bg-blue-600";
        };
    }

    @GetMapping("/dashboard")
    public String dashboard(
            @RequestParam(defaultValue = "productos") String tab,
            HttpSession session,
            Model model) {

        if (!esAdmin(session)) {
            return "redirect:/";
        }

        model.addAttribute("productos", productoRepository.findAll());
        model.addAttribute("usuarios", usuarioRepository.findAll());
        model.addAttribute("activeTab", tab);
        return "admin-dashboard";
    }

    @PostMapping("/productos/crear")
    public String crearProducto(
            @RequestParam String nombre,
            @RequestParam String categoria,
            @RequestParam Integer precio,
            @RequestParam(required = false) Integer precioAnterior,
            @RequestParam String imagen,
            @RequestParam(required = false) String etiqueta,
            @RequestParam(required = false, defaultValue = "bg-white,bg-black") String coloresStr,
            HttpSession session) {

        if (!esAdmin(session)) return "redirect:/";

        String etiquetaLimpia = (etiqueta != null && !etiqueta.isBlank()) ? etiqueta.trim() : null;
        String claseEtiqueta = obtenerClaseEtiqueta(etiquetaLimpia);

        Producto nuevo = new Producto(
                imagen,
                etiquetaLimpia,
                claseEtiqueta,
                categoria,
                nombre,
                precio,
                precioAnterior,
                coloresStr != null && !coloresStr.isBlank() ? coloresStr.trim() : "bg-white,bg-black"
        );
        productoRepository.save(nuevo);
        return "redirect:/admin/dashboard?tab=productos";
    }

    @PostMapping("/productos/actualizar")
    public String actualizarProducto(
            @RequestParam Long id,
            @RequestParam String nombre,
            @RequestParam String categoria,
            @RequestParam Integer precio,
            @RequestParam(required = false) Integer precioAnterior,
            @RequestParam String imagen,
            @RequestParam(required = false) String etiqueta,
            @RequestParam(required = false) String coloresStr,
            HttpSession session) {

        if (!esAdmin(session)) return "redirect:/";

        Producto prod = productoRepository.findById(id).orElse(null);
        if (prod != null) {
            String etiquetaLimpia = (etiqueta != null && !etiqueta.isBlank()) ? etiqueta.trim() : null;
            prod.setNombre(nombre);
            prod.setCategoria(categoria);
            prod.setPrecio(precio);
            prod.setPrecioAnterior(precioAnterior);
            prod.setImagen(imagen);
            prod.setEtiqueta(etiquetaLimpia);
            prod.setClaseEtiqueta(obtenerClaseEtiqueta(etiquetaLimpia));
            prod.setColoresStr(coloresStr != null && !coloresStr.isBlank() ? coloresStr.trim() : "bg-white,bg-black");
            productoRepository.save(prod);
        }
        return "redirect:/admin/dashboard?tab=productos";
    }

    @PostMapping("/productos/eliminar")
    public String eliminarProducto(@RequestParam Long id, HttpSession session) {
        if (!esAdmin(session)) return "redirect:/";
        productoRepository.deleteById(id);
        return "redirect:/admin/dashboard?tab=productos";
    }

    @PostMapping("/usuarios/actualizar")
    public String actualizarUsuario(
            @RequestParam Long id,
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String rol,
            @RequestParam(required = false) String password,
            HttpSession session) {

        if (!esAdmin(session)) return "redirect:/";

        Usuario u = usuarioRepository.findById(id).orElse(null);
        if (u != null) {
            u.setNombre(nombre);
            u.setEmail(email);
            u.setRol(rol);
            if (password != null && !password.isBlank()) {
                u.setPassword(password);
            }
            usuarioRepository.save(u);

            Long idSesion = (Long) session.getAttribute("usuarioId");
            if (idSesion != null && idSesion.equals(u.getId())) {
                session.setAttribute("usuarioNombre", u.getNombre());
                session.setAttribute("usuarioEmail", u.getEmail());
                session.setAttribute("usuarioRol", u.getRol());
            }
        }
        return "redirect:/admin/dashboard?tab=usuarios";
    }

    @PostMapping("/usuarios/eliminar")
    public String eliminarUsuario(@RequestParam Long id, HttpSession session) {
        if (!esAdmin(session)) return "redirect:/";

        Long idSesion = (Long) session.getAttribute("usuarioId");
        if (idSesion == null || !idSesion.equals(id)) {
            usuarioRepository.deleteById(id);
        }
        return "redirect:/admin/dashboard?tab=usuarios";
    }
}