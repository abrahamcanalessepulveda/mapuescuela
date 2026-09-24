
package cl.mapuescuela.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cl.mapuescuela.dto.CrearPedidoRequest;
import cl.mapuescuela.model.Pedido;
import cl.mapuescuela.repository.PedidoRepository;
import cl.mapuescuela.service.PedidoService;
import jakarta.servlet.http.HttpSession;

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
    public List<Pedido> listarPedidos(HttpSession session) {

        if (esAdministrador(session)) {
            return pedidoRepository.findAll();
        }

        Long idCliente = obtenerIdCliente(session);

        return pedidoRepository
                .findByCliente_IdClienteOrderByIdPedidoDesc(idCliente);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPedido(
            @PathVariable Long id,
            HttpSession session) {

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pedido no encontrado"));

        comprobarAccesoAlPedido(session, pedido);

        return ResponseEntity.ok(pedido);
    }

    @PostMapping
    public Pedido crearPedido(
            @RequestBody CrearPedidoRequest request,
            HttpSession session) {

        Long idCliente = obtenerIdCliente(session);

        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe ingresar los datos del pedido");
        }

        // El servidor determina el cliente. No se utiliza
        // el idCliente que pueda enviar el navegador.
        request.setIdCliente(idCliente);

        return pedidoService.crearPedido(request);
    }

    @PutMapping("/{id}/preparar")
    public ResponseEntity<Pedido> iniciarPreparacion(
            @PathVariable Long id,
            HttpSession session) {

        exigirAdministrador(session);

        Pedido pedido = pedidoService.iniciarPreparacion(id);

        return ResponseEntity.ok(pedido);
    }

    @PutMapping("/{id}/listo-retiro")
    public ResponseEntity<Pedido> marcarListoRetiro(
            @PathVariable Long id,
            HttpSession session) {

        exigirAdministrador(session);

        Pedido pedido = pedidoService.marcarListoRetiro(id);

        return ResponseEntity.ok(pedido);
    }

    @PutMapping("/{id}/retirado")
    public ResponseEntity<Pedido> registrarRetiro(
            @PathVariable Long id,
            HttpSession session) {

        exigirAdministrador(session);

        Pedido pedido = pedidoService.registrarRetiro(id);

        return ResponseEntity.ok(pedido);
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Pedido> cancelarPedido(
            @PathVariable Long id,
            HttpSession session) {

        exigirAdministrador(session);

        Pedido pedido = pedidoService.cancelarPedido(id);

        return ResponseEntity.ok(pedido);
    }

    private Long obtenerIdCliente(HttpSession session) {

        Object valor = session.getAttribute("CLIENTE_ID");

        if (valor instanceof Long) {
            return (Long) valor;
        }

        throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Debe iniciar sesión como cliente");
    }

    private boolean esAdministrador(HttpSession session) {

        return Boolean.TRUE.equals(
                session.getAttribute("ADMIN_AUTENTICADO"));
    }

    private void exigirAdministrador(HttpSession session) {

        if (!esAdministrador(session)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Esta acción requiere una sesión administrativa");
        }
    }

    private void comprobarAccesoAlPedido(
            HttpSession session,
            Pedido pedido) {

        if (esAdministrador(session)) {
            return;
        }

        Long idCliente = obtenerIdCliente(session);

        if (pedido.getCliente() == null
                || !idCliente.equals(
                        pedido.getCliente().getIdCliente())) {

            // No revelamos si existe un pedido de otro cliente.
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Pedido no encontrado");
        }
    }
}