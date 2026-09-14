package cl.mapuescuela.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.mapuescuela.model.Despacho;

public interface DespachoRepository extends JpaRepository<Despacho, Long> {
}