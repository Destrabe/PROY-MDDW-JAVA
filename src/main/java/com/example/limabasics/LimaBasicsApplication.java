package com.example.limabasics.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class limaBasicsApplication {

    @GetMapping("/")
    public String inicio() {
        // Devuelve el archivo index.html
        return "index";
    }

    @GetMapping("/contacto")
    public String contacto() {
        return "contact";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contacto";
    }
}