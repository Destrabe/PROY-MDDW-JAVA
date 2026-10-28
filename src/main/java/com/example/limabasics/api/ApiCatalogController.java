package com.example.limabasics.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.limabasics.entities.Producto;
import com.example.limabasics.repositories.ProductoRepository;

@RestController
@RequestMapping("/api")
public class ApiCatalogController {
    private final ProductoRepository productoRepository;

    public ApiCatalogController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @GetMapping("/catalog")
    public List<Producto> obtenerProductosJson() {
        return productoRepository.findAll();
    }

}
