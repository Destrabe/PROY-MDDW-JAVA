package com.example.limabasics.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "carrito_items")
public class CarritoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long usuarioId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    private int cantidad;
    private String color;
    private String talla;

    public CarritoItem() {}

    public CarritoItem(Long usuarioId, Producto producto, int cantidad, String color, String talla) {
        this.usuarioId = usuarioId;
        this.producto = producto;
        this.cantidad = cantidad;
        this.color = (color != null && !color.isEmpty()) ? color : "bg-black";
        this.talla = (talla != null && !talla.isEmpty()) ? talla : "M";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getTalla() { return talla; }
    public void setTalla(String talla) { this.talla = talla; }

    @Transient
    public int getSubtotal() {
        return producto != null ? producto.getPrecio() * cantidad : 0;
    }
}