package com.example.limabasics.controllers;

import com.example.limabasics.entities.Producto;
import com.example.limabasics.repositories.ProductoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class CatalogController {

    private final ProductoRepository productoRepository;

    public CatalogController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @GetMapping("/catalog")
    public String catalog(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false, defaultValue = "300") Integer maxPrecio,
            @RequestParam(required = false, defaultValue = "relevancia") String orden,
            @RequestParam(required = false) String q,
            Model model) {

        List<Producto> productos = productoRepository.findAll();

        if (categoria != null && !categoria.isBlank() && !categoria.equalsIgnoreCase("Todos")) {
            productos = productos.stream()
                    .filter(p -> p.getCategoria() != null && p.getCategoria().equalsIgnoreCase(categoria))
                    .collect(Collectors.toList());
        }

        if (q != null && !q.isBlank()) {
            String query = q.toLowerCase();
            productos = productos.stream()
                    .filter(p -> p.getNombre().toLowerCase().contains(query) || 
                                 (p.getCategoria() != null && p.getCategoria().toLowerCase().contains(query)))
                    .collect(Collectors.toList());
        }

        if (maxPrecio != null) {
            productos = productos.stream()
                    .filter(p -> p.getPrecio() <= maxPrecio)
                    .collect(Collectors.toList());
        }

        if ("menor".equals(orden)) {
            productos.sort(Comparator.comparing(Producto::getPrecio));
        } else if ("mayor".equals(orden)) {
            productos.sort(Comparator.comparing(Producto::getPrecio).reversed());
        } else if ("nuevo".equals(orden)) {
            productos.sort(Comparator.comparing(Producto::getId).reversed());
        }

        model.addAttribute("productos", productos);
        model.addAttribute("categoriaActual", categoria != null ? categoria : "Todos");
        model.addAttribute("maxPrecioActual", maxPrecio);
        model.addAttribute("ordenActual", orden);
        model.addAttribute("busquedaActual", q);

        return "catalog";
    }
}