package cl.mapuescuela.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.mapuescuela.model.DetallePedido;

public interface DetallePedidoRepository
        extends JpaRepository<DetallePedido, Long> {

    List<DetallePedido> findByPedido_IdPedido(Long idPedido);
}