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
        return "index"; // Apunta a index.html
    }
    
    @GetMapping("/catalog")
    public String catalog() {
        return "catalog"; // Apunta a catalog.html
    }

    @GetMapping("/offers")
    public String offers() {
        return "offers"; // Apunta a offers.html
    }

    @GetMapping("/about")
    public String about() {
        return "about"; // Apunta a about.html
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        String[] tiendas = {"Lima Centro", "San Miguel", "SJL"};
        model.addAttribute("sedes", tiendas);
        return "contact"; // Apunta a contact.html
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // Apunta a login.html
    }
}