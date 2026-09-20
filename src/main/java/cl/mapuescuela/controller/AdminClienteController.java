
package cl.mapuescuela.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.mapuescuela.model.Cliente;
import cl.mapuescuela.repository.ClienteRepository;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/admin/clientes")
public class AdminClienteController {

    private final ClienteRepository clienteRepository;

    public AdminClienteController(
            ClienteRepository clienteRepository) {

        this.clienteRepository = clienteRepository;
    }

    @GetMapping
    public ResponseEntity<?> listarClientes(
            HttpSession session) {

        if (!estaAutenticado(session)) {
            return respuestaNoAutorizada();
        }

        List<Cliente> clientes = clienteRepository.findAll();

        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{idCliente}")
    public ResponseEntity<?> obtenerCliente(
            @PathVariable Long idCliente,
            HttpSession session) {

        if (!estaAutenticado(session)) {
            return respuestaNoAutorizada();
        }

        Cliente cliente = clienteRepository.findById(idCliente)
                .orElse(null);

        if (cliente == null) {
            return respuestaError(
                    HttpStatus.NOT_FOUND,
                    "No se encontró el cliente indicado.");
        }

        return ResponseEntity.ok(cliente);
    }

    @PostMapping
    public ResponseEntity<?> crearCliente(
            @RequestBody Cliente datos,
            HttpSession session) {

        if (!estaAutenticado(session)) {
            return respuestaNoAutorizada();
        }

        String error = validarDatos(datos);

        if (error != null) {
            return respuestaError(
                    HttpStatus.BAD_REQUEST,
                    error);
        }

        String rut = datos.getRut().trim();

        if (rutYaRegistrado(rut, null)) {
            return respuestaError(
                    HttpStatus.CONFLICT,
                    "Ya existe un cliente registrado con ese RUT.");
        }

        Cliente cliente = new Cliente();

        copiarDatos(datos, cliente);

        Cliente guardado = clienteRepository.save(cliente);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(guardado);
    }

    @PutMapping("/{idCliente}")
    public ResponseEntity<?> actualizarCliente(
            @PathVariable Long idCliente,
            @RequestBody Cliente datos,
            HttpSession session) {

        if (!estaAutenticado(session)) {
            return respuestaNoAutorizada();
        }

        String error = validarDatos(datos);

        if (error != null) {
            return respuestaError(
                    HttpStatus.BAD_REQUEST,
                    error);
        }

        Cliente cliente = clienteRepository.findById(idCliente)
                .orElse(null);

        if (cliente == null) {
            return respuestaError(
                    HttpStatus.NOT_FOUND,
                    "No se encontró el cliente indicado.");
        }

        String rut = datos.getRut().trim();

        if (rutYaRegistrado(rut, idCliente)) {
            return respuestaError(
                    HttpStatus.CONFLICT,
                    "El RUT ingresado pertenece a otro cliente.");
        }

        copiarDatos(datos, cliente);

        Cliente guardado = clienteRepository.save(cliente);

        return ResponseEntity.ok(guardado);
    }

    private String validarDatos(Cliente datos) {

        if (datos == null) {
            return "Debe ingresar los datos del cliente.";
        }

        if (datos.getRut() == null
                || datos.getRut().isBlank()) {

            return "Debe ingresar el RUT del cliente.";
        }

        if (datos.getRut().trim().length() > 20) {
            return "El RUT no puede superar los 20 caracteres.";
        }

        if (datos.getRazonSocial() == null
                || datos.getRazonSocial().isBlank()) {

            return "Debe ingresar la razón social del cliente.";
        }

        if (datos.getRazonSocial().trim().length() > 150) {
            return "La razón social no puede superar los 150 caracteres.";
        }

        if (datos.getEmail() == null
                || datos.getEmail().isBlank()) {

            return "Debe ingresar el correo electrónico del cliente.";
        }

        if (datos.getEmail().trim().length() > 150) {
            return "El correo electrónico no puede superar los 150 caracteres.";
        }

        if (datos.getNombreContacto() != null
                && datos.getNombreContacto().trim().length() > 150) {

            return "El nombre de contacto no puede superar los 150 caracteres.";
        }

        if (datos.getTelefono() != null
                && datos.getTelefono().trim().length() > 30) {

            return "El teléfono no puede superar los 30 caracteres.";
        }

        if (datos.getDireccion() != null
                && datos.getDireccion().trim().length() > 255) {

            return "La dirección no puede superar los 255 caracteres.";
        }

        return null;
    }

    private boolean rutYaRegistrado(
            String rut,
            Long idClienteActual) {

        return clienteRepository.findAll()
                .stream()
                .anyMatch(cliente ->
                        cliente.getRut() != null
                        && cliente.getRut().trim().equalsIgnoreCase(rut)
                        && (idClienteActual == null
                            || !cliente.getIdCliente().equals(idClienteActual)));
    }

    private void copiarDatos(
            Cliente origen,
            Cliente destino) {

        destino.setRut(origen.getRut().trim());

        destino.setRazonSocial(
                origen.getRazonSocial().trim());

        destino.setNombreContacto(
                limpiarTexto(origen.getNombreContacto()));

        destino.setEmail(origen.getEmail().trim());

        destino.setTelefono(
                limpiarTexto(origen.getTelefono()));

        destino.setDireccion(
                limpiarTexto(origen.getDireccion()));
    }

    private String limpiarTexto(String valor) {

        if (valor == null || valor.isBlank()) {
            return null;
        }

        return valor.trim();
    }

    private boolean estaAutenticado(
            HttpSession session) {

        return Boolean.TRUE.equals(
                session.getAttribute("ADMIN_AUTENTICADO"));
    }

    private ResponseEntity<Map<String, Object>>
            respuestaNoAutorizada() {

        return respuestaError(
                HttpStatus.UNAUTHORIZED,
                "Debe iniciar sesión como administrador.");
    }

    private ResponseEntity<Map<String, Object>> respuestaError(
            HttpStatus estado,
            String mensaje) {

        Map<String, Object> respuesta =
                new LinkedHashMap<>();

        respuesta.put("mensaje", mensaje);

        return ResponseEntity
                .status(estado)
                .body(respuesta);
    }
}