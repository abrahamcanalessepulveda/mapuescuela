package cl.mapuescuela.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import cl.mapuescuela.model.ComprobantePago;
import cl.mapuescuela.model.DetallePedido;
import cl.mapuescuela.model.Pedido;
import cl.mapuescuela.model.Producto;
import cl.mapuescuela.repository.ComprobantePagoRepository;
import cl.mapuescuela.repository.DetallePedidoRepository;
import cl.mapuescuela.repository.PedidoRepository;
import cl.mapuescuela.repository.ProductoRepository;

@Service
public class ComprobantePagoService {

    private final ComprobantePagoRepository comprobantePagoRepository;
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoRepository productoRepository;

    public ComprobantePagoService(
            ComprobantePagoRepository comprobantePagoRepository,
            PedidoRepository pedidoRepository,
            DetallePedidoRepository detallePedidoRepository,
            ProductoRepository productoRepository) {

        this.comprobantePagoRepository = comprobantePagoRepository;
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional
    public ComprobantePago registrarComprobante(
            Long idPedido,
            MultipartFile archivo) {

        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() ->
                        new RuntimeException("Pedido no encontrado"));

        if (!"PENDIENTE_PAGO".equals(pedido.getEstado())) {
            throw new RuntimeException(
                    "El pedido no se encuentra pendiente de pago");
        }

        if (archivo == null || archivo.isEmpty()) {
            throw new RuntimeException(
                    "Debe adjuntar un comprobante de pago");
        }

        try {

            Path carpeta = Paths.get(
                    "uploads",
                    "comprobantes");

            Files.createDirectories(carpeta);

            String nombreOriginal =
                    archivo.getOriginalFilename();

            if (nombreOriginal == null
                    || nombreOriginal.isBlank()) {

                nombreOriginal = "comprobante";
            }

            String nombreArchivo =
                    UUID.randomUUID()
                            + "_"
                            + nombreOriginal;

            Path rutaArchivo =
                    carpeta.resolve(nombreArchivo);

            Files.copy(
                    archivo.getInputStream(),
                    rutaArchivo,
                    StandardCopyOption.REPLACE_EXISTING);

            ComprobantePago comprobante =
                    new ComprobantePago();

            comprobante.setPedido(pedido);
            comprobante.setArchivo(
                    rutaArchivo.toString());
            comprobante.setEstadoValidacion(
                    "PENDIENTE");

            ComprobantePago comprobanteGuardado =
                    comprobantePagoRepository.save(
                            comprobante);

            pedido.setEstado("PAGO_EN_REVISION");
            pedidoRepository.save(pedido);

            return comprobanteGuardado;

        } catch (IOException e) {

            throw new RuntimeException(
                    "Error al guardar el comprobante de pago",
                    e);
        }
    }

    @Transactional
    public ComprobantePago validarComprobante(
            Long idComprobante,
            String resultado,
            String observacion) {

        ComprobantePago comprobante =
                comprobantePagoRepository
                        .findById(idComprobante)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Comprobante no encontrado"));

        Pedido pedido = comprobante.getPedido();

        if (!"PENDIENTE".equals(
                comprobante.getEstadoValidacion())) {

            throw new RuntimeException(
                    "El comprobante ya fue validado");
        }

        if (!"PAGO_EN_REVISION".equals(
                pedido.getEstado())) {

            throw new RuntimeException(
                    "El pedido no se encuentra en revisión de pago");
        }

        if (resultado == null) {

            throw new RuntimeException(
                    "Debe indicar el resultado de la validación");
        }

        if ("APROBADO".equalsIgnoreCase(resultado)) {

            List<DetallePedido> detalles =
                    detallePedidoRepository
                            .findByPedido_IdPedido(
                                    pedido.getIdPedido());

            for (DetallePedido detalle : detalles) {

                Producto producto =
                        detalle.getProducto();

                if (producto.getStock()
                        < detalle.getCantidad()) {

                    throw new RuntimeException(
                            "Stock insuficiente para el producto: "
                                    + producto.getNombre());
                }
            }

            for (DetallePedido detalle : detalles) {

                Producto producto =
                        detalle.getProducto();

                producto.setStock(
                        producto.getStock()
                                - detalle.getCantidad());

                productoRepository.save(producto);
            }

            comprobante.setEstadoValidacion(
                    "APROBADO");

            pedido.setEstado("PAGO_APROBADO");

        } else if ("RECHAZADO".equalsIgnoreCase(
                resultado)) {

            comprobante.setEstadoValidacion(
                    "RECHAZADO");

            pedido.setEstado("PAGO_RECHAZADO");

        } else {

            throw new RuntimeException(
                    "El resultado debe ser APROBADO o RECHAZADO");
        }

        comprobante.setObservacion(observacion);

        pedidoRepository.save(pedido);

        return comprobantePagoRepository.save(
                comprobante);
    }
}