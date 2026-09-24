
package cl.mapuescuela.controller;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.mapuescuela.model.Cliente;
import cl.mapuescuela.repository.ClienteRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/clientes")
public class ClienteAccesoController {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    public ClienteAccesoController(
            ClienteRepository clienteRepository,
            PasswordEncoder passwordEncoder) {

        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/registro")
    public ResponseEntity<Map<String, Object>> registrar(
            @RequestBody RegistroClienteRequest datos) {

        if (datos == null
                || esVacio(datos.rut())
                || esVacio(datos.razonSocial())
                || esVacio(datos.email())
                || esVacio(datos.password())) {

            return respuesta(
                    HttpStatus.BAD_REQUEST,
                    "Debe completar RUT, razón social, correo y contraseña.");
        }

        String rut = datos.rut().trim();
        String razonSocial = datos.razonSocial().trim();
        String email = datos.email().trim().toLowerCase();
        String password = datos.password();

        if (rut.length() > 20
                || razonSocial.length() > 150
                || email.length() > 150
                || longitud(datos.nombreContacto()) > 150
                || longitud(datos.telefono()) > 30
                || longitud(datos.direccion()) > 255) {

            return respuesta(
                    HttpStatus.BAD_REQUEST,
                    "Uno o más campos superan la longitud permitida.");
        }

        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            return respuesta(
                    HttpStatus.BAD_REQUEST,
                    "Debe ingresar un correo electrónico válido.");
        }

        if (password.length() < 8 || password.length() > 72) {
            return respuesta(
                    HttpStatus.BAD_REQUEST,
                    "La contraseña debe tener entre 8 y 72 caracteres.");
        }

        if (clienteRepository.findByRut(rut).isPresent()
                || clienteRepository.findByEmailIgnoreCase(email).isPresent()) {

            return respuesta(
                    HttpStatus.CONFLICT,
                    "El RUT o correo ya está registrado. "
                    + "Si su cuenta fue creada anteriormente, "
                    + "solicite su habilitación al administrador.");
        }

        Cliente cliente = new Cliente();
        cliente.setRut(rut);
        cliente.setRazonSocial(razonSocial);
        cliente.setNombreContacto(limpiar(datos.nombreContacto()));
        cliente.setEmail(email);
        cliente.setTelefono(limpiar(datos.telefono()));
        cliente.setDireccion(limpiar(datos.direccion()));
        cliente.setPasswordHash(passwordEncoder.encode(password));

        try {
            Cliente creado = clienteRepository.saveAndFlush(cliente);

            Map<String, Object> respuesta = new LinkedHashMap<>();
            respuesta.put("mensaje", "Cliente registrado correctamente.");
            respuesta.put("idCliente", creado.getIdCliente());

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(respuesta);

        } catch (DataIntegrityViolationException excepcion) {
            return respuesta(
                    HttpStatus.CONFLICT,
                    "No fue posible registrar la cuenta. "
                    + "Verifique que el RUT y el correo no estén registrados.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> iniciarSesion(
            @RequestBody LoginClienteRequest datos,
            HttpSession session,
            HttpServletRequest httpRequest) {

        if (datos == null
                || esVacio(datos.email())
                || esVacio(datos.password())) {

            return respuesta(
                    HttpStatus.BAD_REQUEST,
                    "Debe ingresar correo y contraseña.");
        }

        String email = datos.email().trim();

        Optional<Cliente> encontrado =
                clienteRepository.findByEmailIgnoreCase(email);

        if (encontrado.isEmpty()) {
            return credencialesIncorrectas();
        }

        Cliente cliente = encontrado.get();

        if (cliente.getPasswordHash() == null
                || cliente.getPasswordHash().isBlank()
                || !passwordEncoder.matches(
                        datos.password(),
                        cliente.getPasswordHash())) {

            return credencialesIncorrectas();
        }

        httpRequest.changeSessionId();

        session.removeAttribute("ADMIN_AUTENTICADO");
        session.setAttribute("CLIENTE_ID", cliente.getIdCliente());

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("autenticado", true);
        respuesta.put("idCliente", cliente.getIdCliente());
        respuesta.put("razonSocial", cliente.getRazonSocial());
        respuesta.put("mensaje", "Inicio de sesión correcto.");

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/sesion")
    public ResponseEntity<Map<String, Object>> consultarSesion(
            HttpSession session) {

        Cliente cliente = clienteDeSesion(session);

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("autenticado", cliente != null);

        if (cliente != null) {
            respuesta.put("idCliente", cliente.getIdCliente());
            respuesta.put("razonSocial", cliente.getRazonSocial());
            respuesta.put("email", cliente.getEmail());
        }

        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> cerrarSesion(
            HttpSession session) {

        session.invalidate();

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("autenticado", false);
        respuesta.put("mensaje", "Sesión cerrada correctamente.");

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/mi-cuenta")
    public ResponseEntity<?> consultarMiCuenta(
            HttpSession session) {

        Cliente cliente = clienteDeSesion(session);

        if (cliente == null) {
            return respuesta(
                    HttpStatus.UNAUTHORIZED,
                    "Debe iniciar sesión como cliente.");
        }

        return ResponseEntity.ok(cliente);
    }

    private Cliente clienteDeSesion(HttpSession session) {

        Object id = session.getAttribute("CLIENTE_ID");

        if (!(id instanceof Long)) {
            return null;
        }

        return clienteRepository
                .findById((Long) id)
                .orElse(null);
    }

    private ResponseEntity<Map<String, Object>>
            credencialesIncorrectas() {

        return respuesta(
                HttpStatus.UNAUTHORIZED,
                "Correo o contraseña incorrectos.");
    }

    private ResponseEntity<Map<String, Object>> respuesta(
            HttpStatus estado,
            String mensaje) {

        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("mensaje", mensaje);

        return ResponseEntity
                .status(estado)
                .body(cuerpo);
    }

    private boolean esVacio(String valor) {
        return valor == null || valor.isBlank();
    }

    private int longitud(String valor) {
        return valor == null ? 0 : valor.trim().length();
    }

    private String limpiar(String valor) {
        return valor == null ? null : valor.trim();
    }

    public record RegistroClienteRequest(
            String rut,
            String razonSocial,
            String nombreContacto,
            String email,
            String telefono,
            String direccion,
            String password) {
    }

    public record LoginClienteRequest(
            String email,
            String password) {
    }
}