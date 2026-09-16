package cl.mapuescuela.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.mapuescuela.model.ComprobantePago;

public interface ComprobantePagoRepository
        extends JpaRepository<ComprobantePago, Long> {

    Optional<ComprobantePago> findByPedidoIdPedido(Long idPedido);
}