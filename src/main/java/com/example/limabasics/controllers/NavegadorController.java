package com.example.limabasics.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class NavegadorController {

    @GetMapping("/")
    public String inicio() {
        return "index";
    }
    
    @GetMapping("/contacto")
    public String contacto(Model model) {
        String[] tiendas = {"Lima Centro", "San Miguel", "SJL"};
        model.addAttribute("sedes", tiendas);
        return "contact";
    }

    @GetMapping("/login")
    public String inicioSesion() {
        return "login";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }
}