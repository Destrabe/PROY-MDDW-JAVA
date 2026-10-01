package com.example.limabasics.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class NavegadorController {

    @GetMapping
    public String inicio(){
        return "inicio";
    }
    
    @GetMapping("/contacto")
    public String contacto(Model model){
        String[] tiendas = {"Lima Centro", "San Miguel", "SJL"};
        model.addAttribute("sedes",tiendas);
        return "contacto";
    }

    
    @GetMapping("/login")
    public String inicioSesion(){
        return "inicio-sesion";
    }
}
