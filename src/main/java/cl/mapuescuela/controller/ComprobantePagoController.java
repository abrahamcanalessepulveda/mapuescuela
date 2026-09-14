package cl.mapuescuela.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cl.mapuescuela.dto.ValidarComprobanteRequest;
import cl.mapuescuela.model.ComprobantePago;
import cl.mapuescuela.service.ComprobantePagoService;

@RestController
@RequestMapping("/api/comprobantes")
public class ComprobantePagoController {

    private final ComprobantePagoService comprobantePagoService;

    public ComprobantePagoController(
            ComprobantePagoService comprobantePagoService) {

        this.comprobantePagoService = comprobantePagoService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> subirComprobante(
            @RequestParam Long idPedido,
            @RequestParam MultipartFile archivo) {

        ComprobantePago comprobante =
                comprobantePagoService.registrarComprobante(
                        idPedido,
                        archivo);

        Map<String, Object> respuesta = new LinkedHashMap<>();

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
            @RequestBody ValidarComprobanteRequest request) {

        ComprobantePago comprobante =
                comprobantePagoService.validarComprobante(
                        id,
                        request.getResultado(),
                        request.getObservacion());

        Map<String, Object> respuesta = new LinkedHashMap<>();

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
}