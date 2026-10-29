package com.example.limabasics.repositories;

import com.example.limabasics.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.detalles d LEFT JOIN FETCH d.producto WHERE p.usuario.id = :usuarioId ORDER BY p.fechaCreacion DESC")
    List<Pedido> findByUsuarioIdWithDetails(@Param("usuarioId") Long usuarioId);

    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.detalles d LEFT JOIN FETCH d.producto ORDER BY p.fechaCreacion DESC")
    List<Pedido> findAllWithDetailsOrderByFechaDesc();
}