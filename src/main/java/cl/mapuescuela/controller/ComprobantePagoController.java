
package cl.mapuescuela.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import cl.mapuescuela.dto.ValidarComprobanteRequest;
import cl.mapuescuela.model.ComprobantePago;
import cl.mapuescuela.model.Pedido;
import cl.mapuescuela.repository.PedidoRepository;
import cl.mapuescuela.service.ComprobantePagoService;
import cl.mapuescuela.service.FlowableService;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/comprobantes")
public class ComprobantePagoController {

    private final ComprobantePagoService comprobantePagoService;
    private final FlowableService flowableService;
    private final PedidoRepository pedidoRepository;

    public ComprobantePagoController(
            ComprobantePagoService comprobantePagoService,
            FlowableService flowableService,
            PedidoRepository pedidoRepository) {

        this.comprobantePagoService = comprobantePagoService;
        this.flowableService = flowableService;
        this.pedidoRepository = pedidoRepository;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> subirComprobante(
            @RequestParam Long idPedido,
            @RequestParam MultipartFile archivo,
            HttpSession session) {

        comprobarAccesoAlPedido(session, idPedido);

        ComprobantePago comprobante =
                comprobantePagoService.registrarComprobante(
                        idPedido,
                        archivo);

        flowableService.registrarIdComprobante(
                idPedido,
                comprobante.getIdComprobante());

        flowableService.completarTarea(
                idPedido,
                "Adjuntar comprobante de pago");

        Map<String, Object> respuesta =
                new LinkedHashMap<>();

        respuesta.put(
                "idComprobante",
                comprobante.getIdComprobante());

        respuesta.put(
                "idPedido",
                comprobante.getPedido().getIdPedido());

        respuesta.put(
                "archivo",
                comprobante.getArchivo());

        respuesta.put(
                "estadoValidacion",
                comprobante.getEstadoValidacion());

        respuesta.put(
                "estadoPedido",
                "PAGO_EN_REVISION");

        return ResponseEntity.ok(respuesta);
    }

    @PutMapping("/{id}/validar")
    public ResponseEntity<Map<String, Object>> validarComprobante(
            @PathVariable Long id,
            @RequestBody ValidarComprobanteRequest request,
            HttpSession session) {

        exigirAdministrador(session);

        ComprobantePago comprobante =
                comprobantePagoService.validarComprobante(
                        id,
                        request.getResultado(),
                        request.getObservacion());

        Map<String, Object> respuesta =
                new LinkedHashMap<>();

        respuesta.put(
                "idComprobante",
                comprobante.getIdComprobante());

        respuesta.put(
                "idPedido",
                comprobante.getPedido().getIdPedido());

        respuesta.put(
                "resultado",
                comprobante.getEstadoValidacion());

        respuesta.put(
                "observacion",
                comprobante.getObservacion());

        respuesta.put(
                "estadoPedido",
                comprobante.getPedido().getEstado());

        return ResponseEntity.ok(respuesta);
    }

    private void comprobarAccesoAlPedido(
            HttpSession session,
            Long idPedido) {

        if (esAdministrador(session)) {
            return;
        }

        Long idCliente = obtenerIdCliente(session);

        Pedido pedido = pedidoRepository
                .findById(idPedido)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pedido no encontrado"));

        if (pedido.getCliente() == null
                || !idCliente.equals(
                        pedido.getCliente().getIdCliente())) {

            // No revelamos si existe un pedido de otro cliente.
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Pedido no encontrado");
        }
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
}