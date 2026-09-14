package cl.mapuescuela.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.mapuescuela.dto.CrearPedidoRequest;
import cl.mapuescuela.model.Pedido;
import cl.mapuescuela.repository.PedidoRepository;
import cl.mapuescuela.service.PedidoService;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final PedidoService pedidoService;

    public PedidoController(
            PedidoRepository pedidoRepository,
            PedidoService pedidoService) {

        this.pedidoRepository = pedidoRepository;
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPedido(
            @PathVariable Long id) {

        return pedidoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Pedido crearPedido(
            @RequestBody CrearPedidoRequest request) {

        return pedidoService.crearPedido(request);
    }

    @PutMapping("/{id}/preparar")
    public ResponseEntity<Pedido> iniciarPreparacion(
            @PathVariable Long id) {

        Pedido pedido =
                pedidoService.iniciarPreparacion(id);

        return ResponseEntity.ok(pedido);
    }

    @PutMapping("/{id}/listo-retiro")
    public ResponseEntity<Pedido> marcarListoRetiro(
            @PathVariable Long id) {

        Pedido pedido =
                pedidoService.marcarListoRetiro(id);

        return ResponseEntity.ok(pedido);
    }

    @PutMapping("/{id}/retirado")
    public ResponseEntity<Pedido> registrarRetiro(
            @PathVariable Long id) {

        Pedido pedido =
                pedidoService.registrarRetiro(id);

        return ResponseEntity.ok(pedido);
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Pedido> cancelarPedido(
            @PathVariable Long id) {

        Pedido pedido =
                pedidoService.cancelarPedido(id);

        return ResponseEntity.ok(pedido);
    }
}