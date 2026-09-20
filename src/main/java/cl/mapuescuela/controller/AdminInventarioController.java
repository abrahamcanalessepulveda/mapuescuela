package cl.mapuescuela.controller;

import java.math.BigDecimal;
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

import cl.mapuescuela.model.Producto;
import cl.mapuescuela.repository.ProductoRepository;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/admin/inventario")
public class AdminInventarioController {

    private final ProductoRepository productoRepository;

    public AdminInventarioController(
            ProductoRepository productoRepository) {

        this.productoRepository = productoRepository;
    }

    @GetMapping
    public ResponseEntity<?> listarProductos(
            HttpSession session) {

        if (!estaAutenticado(session)) {
            return respuestaNoAutorizada();
        }

        List<Producto> productos =
                productoRepository.findAll();

        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{idProducto}")
    public ResponseEntity<?> obtenerProducto(
            @PathVariable Long idProducto,
            HttpSession session) {

        if (!estaAutenticado(session)) {
            return respuestaNoAutorizada();
        }

        Producto producto =
                productoRepository.findById(idProducto)
                        .orElse(null);

        if (producto == null) {
            return respuestaError(
                    HttpStatus.NOT_FOUND,
                    "No se encontró el producto indicado.");
        }

        return ResponseEntity.ok(producto);
    }

    @PostMapping
    public ResponseEntity<?> crearProducto(
            @RequestBody Producto datos,
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

        Producto producto = new Producto();

        copiarDatos(datos, producto);

        Producto guardado =
                productoRepository.save(producto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(guardado);
    }

    @PutMapping("/{idProducto}")
    public ResponseEntity<?> actualizarProducto(
            @PathVariable Long idProducto,
            @RequestBody Producto datos,
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

        Producto producto =
                productoRepository.findById(idProducto)
                        .orElse(null);

        if (producto == null) {
            return respuestaError(
                    HttpStatus.NOT_FOUND,
                    "No se encontró el producto indicado.");
        }

        copiarDatos(datos, producto);

        Producto guardado =
                productoRepository.save(producto);

        return ResponseEntity.ok(guardado);
    }

    private String validarDatos(Producto datos) {

        if (datos == null) {
            return "Debe ingresar los datos del producto.";
        }

        if (datos.getNombre() == null
                || datos.getNombre().isBlank()) {

            return "Debe ingresar el nombre del producto.";
        }

        if (datos.getPrecio() == null
                || datos.getPrecio().compareTo(BigDecimal.ZERO) < 0) {

            return "El precio debe ser mayor o igual a cero.";
        }

        if (datos.getStock() == null
                || datos.getStock() < 0) {

            return "El stock debe ser mayor o igual a cero.";
        }

        if (datos.getEstado() == null
                || datos.getEstado().isBlank()) {

            return "Debe indicar el estado del producto.";
        }

        if (!"DISPONIBLE".equals(datos.getEstado())
                && !"NO_DISPONIBLE".equals(datos.getEstado())) {

            return "El estado debe ser DISPONIBLE o NO_DISPONIBLE.";
        }

        return null;
    }

    private void copiarDatos(
            Producto origen,
            Producto destino) {

        destino.setNombre(
                origen.getNombre().trim());

        destino.setDescripcion(
                origen.getDescripcion());

        destino.setCategoria(
                origen.getCategoria());

        destino.setPrecio(
                origen.getPrecio());

        destino.setStock(
                origen.getStock());

        destino.setEstado(
                origen.getEstado());

        destino.setImagen(
                origen.getImagen());
    }

    private boolean estaAutenticado(
            HttpSession session) {

        Object valor =
                session.getAttribute("ADMIN_AUTENTICADO");

        return Boolean.TRUE.equals(valor);
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