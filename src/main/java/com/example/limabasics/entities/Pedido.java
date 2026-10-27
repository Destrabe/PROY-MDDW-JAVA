package com.example.limabasics.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long usuarioId;

    @Column(columnDefinition = "TEXT")
    private String detallesItems; 

    private Double total;

    private String estado;

    private LocalDateTime fechaCreacion;

    public Pedido() {
        this.fechaCreacion = LocalDateTime.now();
        this.estado = "Completado";
    }

    public Pedido(Long usuarioId, String detallesItems, Double total) {
        this.usuarioId = usuarioId;
        this.detallesItems = detallesItems;
        this.total = total;
        this.fechaCreacion = LocalDateTime.now();
        this.estado = "Completado";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getDetallesItems() { return detallesItems; }
    public void setDetallesItems(String detallesItems) { this.detallesItems = detallesItems; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}