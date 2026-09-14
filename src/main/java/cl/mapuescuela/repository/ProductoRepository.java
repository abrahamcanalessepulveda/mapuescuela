package cl.mapuescuela.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.mapuescuela.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}