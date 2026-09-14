package cl.mapuescuela.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.mapuescuela.dto.RegistrarDespachoRequest;
import cl.mapuescuela.model.Despacho;
import cl.mapuescuela.service.DespachoService;

@RestController
@RequestMapping("/api/despachos")
public class DespachoController {

    private final DespachoService despachoService;

    public DespachoController(DespachoService despachoService) {
        this.despachoService = despachoService;
    }

    @PostMapping("/{idPedido}/enviar")
    public ResponseEntity<Map<String, Object>> registrarEnvio(
            @PathVariable Long idPedido,
            @RequestBody RegistrarDespachoRequest request) {

        Despacho despacho =
                despachoService.registrarEnvio(idPedido, request);

        Map<String, Object> respuesta = new LinkedHashMap<>();

        respuesta.put("idDespacho", despacho.getIdDespacho());
        respuesta.put("idPedido", despacho.getPedido().getIdPedido());
        respuesta.put("tipoEntrega", despacho.getTipoEntrega());
        respuesta.put("empresaTransporte", despacho.getEmpresaTransporte());
        respuesta.put("numeroSeguimiento", despacho.getNumeroSeguimiento());
        respuesta.put("fechaEnvio", despacho.getFechaEnvio());
        respuesta.put("estadoPedido", "ENVIADO");

        return ResponseEntity.ok(respuesta);
    }
    @PutMapping("/{idPedido}/entregado")
public ResponseEntity<Map<String, Object>> registrarEntrega(
        @PathVariable Long idPedido) {

    Despacho despacho =
            despachoService.registrarEntrega(idPedido);

    Map<String, Object> respuesta = new LinkedHashMap<>();

    respuesta.put("idDespacho", despacho.getIdDespacho());
    respuesta.put("idPedido", despacho.getPedido().getIdPedido());
    respuesta.put("fechaEntrega", despacho.getFechaEntrega());
    respuesta.put("estadoPedido", "FINALIZADO");

    return ResponseEntity.ok(respuesta);
}
}