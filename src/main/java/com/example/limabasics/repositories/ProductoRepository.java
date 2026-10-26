package com.example.limabasics.repositories;

import com.example.limabasics.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByCategoria(String categoria);

    List<Producto> findByPrecioAnteriorIsNotNull();

    @Query(value = "SELECT * FROM productos WHERE categoria = :categoria", nativeQuery = true)
    List<Producto> buscarProductosPorCategoriaNativo(@Param("categoria") String categoria);
}