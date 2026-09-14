package cl.mapuescuela.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.mapuescuela.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}