package com.example.limabasics.entities;

import jakarta.persistence.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String categoria;
    private Integer precio;
    private Integer precioAnterior;
    private String imagen;
    private String etiqueta;
    private String claseEtiqueta;
    private String coloresStr;

    public Producto() {}

    public Producto(String imagen, String etiqueta, String claseEtiqueta, String categoria, 
                    String nombre, Integer precio, Integer precioAnterior, String coloresStr) {
        this.imagen = imagen;
        this.etiqueta = etiqueta;
        this.claseEtiqueta = claseEtiqueta;
        this.categoria = categoria;
        this.nombre = nombre;
        this.precio = precio;
        this.precioAnterior = precioAnterior;
        this.coloresStr = coloresStr;
    }

    @Transient
    public List<String> getColores() {
        if (coloresStr == null || coloresStr.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.asList(coloresStr.split(","));
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public Integer getPrecio() { return precio; }
    public void setPrecio(Integer precio) { this.precio = precio; }
    public Integer getPrecioAnterior() { return precioAnterior; }
    public void setPrecioAnterior(Integer precioAnterior) { this.precioAnterior = precioAnterior; }
    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }
    public String getEtiqueta() { return etiqueta; }
    public void setEtiqueta(String etiqueta) { this.etiqueta = etiqueta; }
    public String getClaseEtiqueta() { return claseEtiqueta; }
    public void setClaseEtiqueta(String claseEtiqueta) { this.claseEtiqueta = claseEtiqueta; }
    public String getColoresStr() { return coloresStr; }
    public void setColoresStr(String coloresStr) { this.coloresStr = coloresStr; }
}