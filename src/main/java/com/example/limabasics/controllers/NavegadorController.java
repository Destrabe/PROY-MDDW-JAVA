package com.example.limabasics.controllers;

import com.example.limabasics.repositories.ProductoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class NavegadorController {

    private final ProductoRepository productoRepository;

    public NavegadorController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @GetMapping("/")
    public String inicio() {
        return "index";
    }

    @GetMapping("/offers")
    public String offers(Model model) {
        model.addAttribute("ofertas", productoRepository.findByPrecioAnteriorIsNotNull());
        return "offers"; 
    }

    @GetMapping("/about")
    public String about() {
        return "about"; 
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        String[] tiendas = {"Lima Centro", "San Miguel", "SJL"};
        model.addAttribute("sedes", tiendas);
        return "contact"; 
    }
}