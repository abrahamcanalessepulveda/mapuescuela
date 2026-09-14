package cl.mapuescuela.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.mapuescuela.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}