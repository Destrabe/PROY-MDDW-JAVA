package com.example.limabasics.repositories;

import com.example.limabasics.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);
    List<Pedido> findAllByOrderByFechaCreacionDesc(); 
}