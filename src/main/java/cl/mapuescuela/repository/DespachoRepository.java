package cl.mapuescuela.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.mapuescuela.model.Despacho;

public interface DespachoRepository extends JpaRepository<Despacho, Long> {

    Optional<Despacho> findByPedidoIdPedido(Long idPedido);
}