
package cl.mapuescuela.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.mapuescuela.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByEmailIgnoreCase(String email);

    Optional<Cliente> findByRut(String rut);

}