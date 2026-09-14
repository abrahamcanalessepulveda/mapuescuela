package cl.mapuescuela.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.mapuescuela.model.ComprobantePago;

public interface ComprobantePagoRepository extends JpaRepository<ComprobantePago, Long> {
}