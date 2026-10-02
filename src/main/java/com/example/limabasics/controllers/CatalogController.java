package com.example.limabasics.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class CatalogController {

    @GetMapping("/catalog")
    public String catalog(Model model) {
        
        List<Producto> listaProductos = List.of(
            new Producto("https://images.unsplash.com/photo-1529374255404-311a2a4f1fd9?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80", "Nuevo", "bg-[#10b981]", "Polos", "Polo Lima Oversize", 89, 119, List.of("bg-white", "bg-black", "bg-[#ddbea9]")),
            new Producto("https://images.unsplash.com/photo-1556821840-3a63f95609a7?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80", "Más vendido", "bg-blue-600", "Hoodies", "Hoodie Oversize Cyberpunk", 149, null, List.of("bg-gray-400", "bg-white", "bg-black")),
            new Producto("https://images.unsplash.com/photo-1517438476312-10d79c077509?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80", null, null, "Pantalones", "Pantalon Cargo y2k", 129, null, List.of("bg-black", "bg-[#8b7355]", "bg-gray-500")),
            new Producto("https://images.unsplash.com/photo-1551028719-00167b16eac5?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80", "Últimas unidades", "bg-[#1f2937]", "Chaquetas", "Chaqueta Cuero Relaxed", 179, null, List.of("bg-[#6b705c]", "bg-gray-200", "bg-black")),
            new Producto("https://images.unsplash.com/photo-1578681994506-b8f463449011?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80", "Más vendido", "bg-blue-600", "Polos", "Polo Streetwear Oversize", 79, null, List.of("bg-white", "bg-black")),
            new Producto("https://images.unsplash.com/photo-1591047139829-d91aecb6caea?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80", "Nuevo", "bg-[#10b981]", "Chaquetas", "Chaqueta Miraflores", 229, 279, List.of("bg-black", "bg-[#6b705c]", "bg-[#1b263b]"))
        );

        model.addAttribute("productos", listaProductos);
        return "catalog";
    }

    public record Producto(String imagen, String etiqueta, String claseEtiqueta, String categoria, String nombre, Integer precio, Integer precioAnterior, List<String> colores) {}
}