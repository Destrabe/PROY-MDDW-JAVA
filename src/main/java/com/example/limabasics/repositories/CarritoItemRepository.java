package com.example.limabasics.repositories;

import com.example.limabasics.entities.CarritoItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {

    List<CarritoItem> findByUsuario_Id(Long usuarioId);

    Optional<CarritoItem> findByUsuario_IdAndProducto_IdAndColorAndTalla(Long usuarioId, Long productoId, String color,
            String talla);
}