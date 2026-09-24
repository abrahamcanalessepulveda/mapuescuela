
package cl.mapuescuela.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.mapuescuela.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByCliente_IdClienteOrderByIdPedidoDesc(Long idCliente);

}