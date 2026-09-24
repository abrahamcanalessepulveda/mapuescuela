package cl.mapuescuela.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.mapuescuela.dto.RegistrarDespachoRequest;
import cl.mapuescuela.model.ComprobantePago;
import cl.mapuescuela.model.Despacho;
import cl.mapuescuela.model.Pedido;
import cl.mapuescuela.repository.ComprobantePagoRepository;
import cl.mapuescuela.repository.DespachoRepository;
import cl.mapuescuela.repository.PedidoRepository;
import cl.mapuescuela.service.DespachoService;
import cl.mapuescuela.service.FlowableService;
import cl.mapuescuela.service.PedidoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final FlowableService flowableService;
    private final PedidoRepository pedidoRepository;
    private final PedidoService pedidoService;
    private final DespachoService despachoService;
    private final DespachoRepository despachoRepository;
    private final ComprobantePagoRepository comprobantePagoRepository;

    @Value("${admin.username:admin}")
    private String adminUsername;

    @Value("${admin.password:}")
    private String adminPassword;

    public AdminController(
            FlowableService flowableService,
            PedidoRepository pedidoRepository,
            PedidoService pedidoService,
            DespachoService despachoService,
            DespachoRepository despachoRepository,
            ComprobantePagoRepository comprobantePagoRepository) {

        this.flowableService = flowableService;
        this.pedidoRepository = pedidoRepository;
        this.pedidoService = pedidoService;
        this.despachoService = despachoService;
        this.despachoRepository = despachoRepository;
        this.comprobantePagoRepository = comprobantePagoRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody Map<String, String> request,
            HttpSession session,
            HttpServletRequest httpRequest) {

        String username = request.get("username");
        String password = request.get("password");

        if (adminPassword == null
                || adminPassword.isBlank()) {

            Map<String, Object> respuesta =
                    new LinkedHashMap<>();

            respuesta.put("autenticado", false);
            respuesta.put(
                    "mensaje",
                    "La contraseña administrativa no está configurada");

            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(respuesta);
        }

        if (adminUsername.equals(username)
                && adminPassword.equals(password)) {

            httpRequest.changeSessionId();

            session.setAttribute(
                    "ADMIN_AUTENTICADO",
                    true);

            Map<String, Object> respuesta =
                    new LinkedHashMap<>();

            respuesta.put("autenticado", true);
            respuesta.put(
                    "mensaje",
                    "Inicio de sesión correcto");

            return ResponseEntity.ok(respuesta);
        }

        Map<String, Object> respuesta =
                new LinkedHashMap<>();

        respuesta.put("autenticado", false);
        respuesta.put(
                "mensaje",
                "Usuario o contraseña incorrectos");

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(respuesta);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(
            HttpSession session) {

        session.invalidate();

        Map<String, Object> respuesta =
                new LinkedHashMap<>();

        respuesta.put("autenticado", false);
        respuesta.put(
                "mensaje",
                "Sesión cerrada correctamente");

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/sesion")
    public ResponseEntity<Map<String, Object>> consultarSesion(
            HttpSession session) {

        boolean autenticado =
                estaAutenticado(session);

        Map<String, Object> respuesta =
                new LinkedHashMap<>();

        respuesta.put(
                "autenticado",
                autenticado);

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/pedidos/{idPedido}/tareas")
    public ResponseEntity<Map<String, Object>> obtenerTareas(
            @PathVariable Long idPedido,
            HttpSession session) {

        if (!estaAutenticado(session)) {
            return respuestaNoAutorizada();
        }

        Pedido pedido =
                pedidoRepository
                        .findById(idPedido)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "No existe el pedido N.º "
                                                + idPedido));

        String estado =
                pedido.getEstado();

        List<Map<String, Object>> tareas;

        if ("FINALIZADO".equals(estado)
                || "CANCELADO".equals(estado)) {

            tareas = List.of();

        } else {

            tareas =
                    flowableService.obtenerTareasActivas(
                            idPedido);
        }

        Map<String, Object> respuesta =
                new LinkedHashMap<>();

        respuesta.put(
                "idPedido",
                pedido.getIdPedido());

        respuesta.put(
                "estado",
                estado);

        respuesta.put(
                "modalidadEntrega",
                pedido.getModalidadEntrega());

        respuesta.put(
                "cantidadTareas",
                tareas.size());

        respuesta.put(
                "nombrePersonaRetira",
                pedido.getNombrePersonaRetira());

        respuesta.put(
                "rutPersonaRetira",
                pedido.getRutPersonaRetira());

        respuesta.put(
                "fechaRetiro",
                pedido.getFechaRetiro());

        Despacho despacho =
                despachoRepository
                        .findByPedidoIdPedido(idPedido)
                        .orElse(null);

        if (despacho != null) {

            respuesta.put(
                    "empresaTransporte",
                    despacho.getEmpresaTransporte());

            respuesta.put(
                    "numeroSeguimiento",
                    despacho.getNumeroSeguimiento());

            respuesta.put(
                    "fechaEnvio",
                    despacho.getFechaEnvio());

            respuesta.put(
                    "fechaEntrega",
                    despacho.getFechaEntrega());

        } else {

            respuesta.put(
                    "empresaTransporte",
                    null);

            respuesta.put(
                    "numeroSeguimiento",
                    null);

            respuesta.put(
                    "fechaEnvio",
                    null);

            respuesta.put(
                    "fechaEntrega",
                    null);
        }

        ComprobantePago comprobante =
                comprobantePagoRepository
                        .findByPedidoIdPedido(idPedido)
                        .orElse(null);

        if (comprobante != null) {

            respuesta.put(
                    "idComprobante",
                    comprobante.getIdComprobante());

            respuesta.put(
                    "fechaCargaComprobante",
                    comprobante.getFechaCarga());

            respuesta.put(
                    "estadoValidacionComprobante",
                    comprobante.getEstadoValidacion());

            respuesta.put(
                    "observacionComprobante",
                    comprobante.getObservacion());

            respuesta.put(
                    "hayComprobante",
                    true);

        } else {

            respuesta.put(
                    "idComprobante",
                    null);

            respuesta.put(
                    "fechaCargaComprobante",
                    null);

            respuesta.put(
                    "estadoValidacionComprobante",
                    null);

            respuesta.put(
                    "observacionComprobante",
                    null);

            respuesta.put(
                    "hayComprobante",
                    false);
        }

        if (!tareas.isEmpty()) {

            Map<String, Object> tarea =
                    tareas.get(0);

            respuesta.put(
                    "accionPendiente",
                    tarea.get("name"));

        } else {

            respuesta.put(
                    "accionPendiente",
                    null);
        }

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/comprobantes/{idComprobante}/archivo")
    public ResponseEntity<Resource> verComprobante(
            @PathVariable Long idComprobante,
            HttpSession session) {

        if (!estaAutenticado(session)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        try {

            ComprobantePago comprobante =
                    comprobantePagoRepository
                            .findById(idComprobante)
                            .orElseThrow(
                                    () -> new RuntimeException(
                                            "Comprobante no encontrado"));

            Path rutaArchivo =
                    Paths.get(
                            comprobante.getArchivo())
                            .toAbsolutePath()
                            .normalize();

            if (!Files.exists(rutaArchivo)
                    || !Files.isRegularFile(rutaArchivo)) {

                return ResponseEntity
                        .notFound()
                        .build();
            }

            Resource recurso =
                    new UrlResource(
                            rutaArchivo.toUri());

            String tipoContenido =
                    Files.probeContentType(
                            rutaArchivo);

            MediaType mediaType =
                    MediaType.APPLICATION_OCTET_STREAM;

            if (tipoContenido != null) {
                try {
                    mediaType =
                            MediaType.parseMediaType(
                                    tipoContenido);
                } catch (Exception e) {
                    mediaType =
                            MediaType.APPLICATION_OCTET_STREAM;
                }
            }

            String nombreArchivo =
                    rutaArchivo
                            .getFileName()
                            .toString();

            return ResponseEntity
                    .ok()
                    .contentType(mediaType)
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\""
                                    + nombreArchivo
                                    + "\"")
                    .body(recurso);

        } catch (Exception e) {

            return ResponseEntity
                    .notFound()
                    .build();
        }
    }

    @PostMapping("/pedidos/{idPedido}/completar")
    public ResponseEntity<Map<String, Object>> completarTarea(
            @PathVariable Long idPedido,
            @RequestBody Map<String, Object> request,
            HttpSession session) {

        if (!estaAutenticado(session)) {
            return respuestaNoAutorizada();
        }

        Object nombreObjeto =
                request.get("nombreTarea");

        if (nombreObjeto == null
                || nombreObjeto.toString().isBlank()) {

            throw new RuntimeException(
                    "Debe indicar el nombre de la tarea");
        }

        String nombreTarea =
                nombreObjeto.toString();

        Map<String, Object> variables =
                obtenerVariables(request);

        if ("Empacar y preparar pedido".equals(nombreTarea)) {
            pedidoService.iniciarPreparacion(idPedido);
        }

        if ("Registrar pedido disponible para retiro en local"
                .equals(nombreTarea)) {
            pedidoService.marcarListoRetiro(idPedido);
        }

        if ("Confirmar entrega del pedido".equals(nombreTarea)) {
            despachoService.registrarEntrega(idPedido);
        }

        flowableService.completarTarea(
                idPedido,
                nombreTarea,
                variables);

        Map<String, Object> respuesta =
                new LinkedHashMap<>();

        respuesta.put(
                "idPedido",
                idPedido);

        respuesta.put(
                "mensaje",
                "Acción realizada correctamente");

        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/pedidos/{idPedido}/registrar-retiro")
    public ResponseEntity<Map<String, Object>> registrarRetiro(
            @PathVariable Long idPedido,
            @RequestBody Map<String, String> request,
            HttpSession session) {

        if (!estaAutenticado(session)) {
            return respuestaNoAutorizada();
        }

        String nombrePersonaRetira =
                request.get("nombrePersonaRetira");

        String rutPersonaRetira =
                request.get("rutPersonaRetira");

        pedidoService.registrarDatosRetiro(
                idPedido,
                nombrePersonaRetira,
                rutPersonaRetira);

        Pedido pedido =
                pedidoService.registrarRetiro(
                        idPedido);

        flowableService.completarTarea(
                idPedido,
                "Registrar retiro en el local");

        Map<String, Object> respuesta =
                new LinkedHashMap<>();

        respuesta.put(
                "idPedido",
                pedido.getIdPedido());

        respuesta.put(
                "nombrePersonaRetira",
                pedido.getNombrePersonaRetira());

        respuesta.put(
                "rutPersonaRetira",
                pedido.getRutPersonaRetira());

        respuesta.put(
                "fechaRetiro",
                pedido.getFechaRetiro());

        respuesta.put(
                "mensaje",
                "Retiro registrado correctamente");

        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/pedidos/{idPedido}/registrar-despacho")
    public ResponseEntity<Map<String, Object>> registrarDespacho(
            @PathVariable Long idPedido,
            @RequestBody RegistrarDespachoRequest request,
            HttpSession session) {

        if (!estaAutenticado(session)) {
            return respuestaNoAutorizada();
        }

        despachoService.registrarDatosEnvio(
                idPedido,
                request);

        Despacho despacho =
                despachoService.registrarEnvio(
                        idPedido,
                        request);

        Map<String, Object> variables =
                new LinkedHashMap<>();

        variables.put(
                "numeroSeguimiento",
                despacho.getNumeroSeguimiento());

        flowableService.completarTarea(
                idPedido,
                "Registrar envío y número de seguimiento",
                variables);

        Map<String, Object> respuesta =
                new LinkedHashMap<>();

        respuesta.put(
                "idPedido",
                idPedido);

        respuesta.put(
                "idDespacho",
                despacho.getIdDespacho());

        respuesta.put(
                "empresaTransporte",
                despacho.getEmpresaTransporte());

        respuesta.put(
                "numeroSeguimiento",
                despacho.getNumeroSeguimiento());

        respuesta.put(
                "fechaEnvio",
                despacho.getFechaEnvio());

        respuesta.put(
                "mensaje",
                "Despacho registrado correctamente");

        return ResponseEntity.ok(respuesta);
    }

    private boolean estaAutenticado(
            HttpSession session) {

        Object valor =
                session.getAttribute(
                        "ADMIN_AUTENTICADO");

        return Boolean.TRUE.equals(valor);
    }

    private ResponseEntity<Map<String, Object>>
            respuestaNoAutorizada() {

        Map<String, Object> respuesta =
                new LinkedHashMap<>();

        respuesta.put(
                "autenticado",
                false);

        respuesta.put(
                "mensaje",
                "Debe iniciar sesión como administrador");

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(respuesta);
    }

    private Map<String, Object> obtenerVariables(
            Map<String, Object> request) {

        Map<String, Object> variables =
                new LinkedHashMap<>();

        Object variablesObjeto =
                request.get("variables");

        if (!(variablesObjeto instanceof Map<?, ?>)) {
            return variables;
        }

        Map<?, ?> variablesRecibidas =
                (Map<?, ?>) variablesObjeto;

        for (Map.Entry<?, ?> entry
                : variablesRecibidas.entrySet()) {

            if (entry.getKey() != null) {

                variables.put(
                        entry.getKey().toString(),
                        entry.getValue());
            }
        }

        return variables;
    }
}