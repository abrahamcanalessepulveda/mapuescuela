package cl.mapuescuela.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.mapuescuela.dto.CrearPedidoRequest;
import cl.mapuescuela.dto.ItemPedidoRequest;
import cl.mapuescuela.model.Cliente;
import cl.mapuescuela.model.DetallePedido;
import cl.mapuescuela.model.Pedido;
import cl.mapuescuela.model.Producto;
import cl.mapuescuela.repository.ClienteRepository;
import cl.mapuescuela.repository.DetallePedidoRepository;
import cl.mapuescuela.repository.PedidoRepository;
import cl.mapuescuela.repository.ProductoRepository;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final FlowableService flowableService;

    public PedidoService(
            PedidoRepository pedidoRepository,
            ClienteRepository clienteRepository,
            ProductoRepository productoRepository,
            DetallePedidoRepository detallePedidoRepository,
            FlowableService flowableService) {

        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.flowableService = flowableService;
    }

    @Transactional
    public Pedido crearPedido(CrearPedidoRequest request) {

        Cliente cliente = clienteRepository.findById(request.getIdCliente())
                .orElseThrow(() ->
                        new RuntimeException("Cliente no encontrado"));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setModalidadEntrega(request.getModalidadEntrega());
        pedido.setEstado("PENDIENTE_PAGO");

        BigDecimal total = BigDecimal.ZERO;

        for (ItemPedidoRequest item : request.getProductos()) {

            Producto producto = productoRepository.findById(item.getIdProducto())
                    .orElseThrow(() ->
                            new RuntimeException("Producto no encontrado"));

            if (item.getCantidad() == null || item.getCantidad() <= 0) {
                throw new RuntimeException(
                        "La cantidad debe ser mayor que cero");
            }

            if (producto.getStock() < item.getCantidad()) {
                throw new RuntimeException(
                        "Stock insuficiente para el producto: "
                                + producto.getNombre());
            }

            BigDecimal subtotal = producto.getPrecio()
                    .multiply(BigDecimal.valueOf(item.getCantidad()));

            total = total.add(subtotal);
        }

        pedido.setTotal(total);
        pedido = pedidoRepository.save(pedido);

        for (ItemPedidoRequest item : request.getProductos()) {

            Producto producto = productoRepository.findById(item.getIdProducto())
                    .orElseThrow(() ->
                            new RuntimeException("Producto no encontrado"));

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());

            detallePedidoRepository.save(detalle);
        }

        String modalidadFlowable =
                convertirModalidadParaFlowable(
                        pedido.getModalidadEntrega());

        String processInstanceId =
                flowableService.iniciarProceso(
                        pedido.getIdPedido(),
                        modalidadFlowable);

        pedido.setFlowableProcessInstanceId(
                processInstanceId);

        pedido = pedidoRepository.save(pedido);

        flowableService.completarTarea(
                pedido.getIdPedido(),
                "Registrar datos de compra y generar pedido");

        flowableService.completarTarea(
                pedido.getIdPedido(),
                "Notificar datos de transferencia");

        return pedido;
    }

    private String convertirModalidadParaFlowable(
            String modalidadEntrega) {

        if (modalidadEntrega == null) {
            throw new RuntimeException(
                    "La modalidad de entrega es obligatoria");
        }

        if ("RETIRO".equalsIgnoreCase(modalidadEntrega)
                || "retiroLocal".equalsIgnoreCase(modalidadEntrega)) {

            return "retiroLocal";
        }

        if ("DESPACHO".equalsIgnoreCase(modalidadEntrega)
                || "despachoDomicilio".equalsIgnoreCase(modalidadEntrega)) {

            return "despachoDomicilio";
        }

        throw new RuntimeException(
                "Modalidad de entrega no válida: "
                        + modalidadEntrega);
    }

    @Transactional
    public Pedido iniciarPreparacion(Long idPedido) {

        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() ->
                        new RuntimeException("Pedido no encontrado"));

        if (!"PAGO_APROBADO".equals(pedido.getEstado())) {
            throw new RuntimeException(
                    "El pedido debe estar con pago aprobado para iniciar preparación");
        }

        pedido.setEstado("EN_PREPARACION");

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido marcarListoRetiro(Long idPedido) {

        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() ->
                        new RuntimeException("Pedido no encontrado"));

        if (!"EN_PREPARACION".equals(pedido.getEstado())) {
            throw new RuntimeException(
                    "El pedido debe estar en preparación");
        }

        if (!"RETIRO".equalsIgnoreCase(
                pedido.getModalidadEntrega())) {

            throw new RuntimeException(
                    "El pedido no corresponde a modalidad RETIRO");
        }

        pedido.setEstado("LISTO_RETIRO");

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido registrarDatosRetiro(
            Long idPedido,
            String nombrePersonaRetira,
            String rutPersonaRetira) {

        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() ->
                        new RuntimeException("Pedido no encontrado"));

        if (!"LISTO_RETIRO".equals(pedido.getEstado())) {
            throw new RuntimeException(
                    "El pedido debe estar listo para retiro");
        }

        if (!"RETIRO".equalsIgnoreCase(
                pedido.getModalidadEntrega())) {

            throw new RuntimeException(
                    "El pedido no corresponde a modalidad RETIRO");
        }

        if (nombrePersonaRetira == null
                || nombrePersonaRetira.trim().isEmpty()) {

            throw new RuntimeException(
                    "Debe ingresar el nombre de la persona que retira");
        }

        if (rutPersonaRetira == null
                || rutPersonaRetira.trim().isEmpty()) {

            throw new RuntimeException(
                    "Debe ingresar el RUT de la persona que retira");
        }

        pedido.setNombrePersonaRetira(
                nombrePersonaRetira.trim());

        pedido.setRutPersonaRetira(
                rutPersonaRetira.trim());

        pedido.setFechaRetiro(
                LocalDateTime.now());

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido registrarRetiro(Long idPedido) {

        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() ->
                        new RuntimeException("Pedido no encontrado"));

        if (!"LISTO_RETIRO".equals(pedido.getEstado())) {
            throw new RuntimeException(
                    "El pedido debe estar listo para retiro");
        }

        if (!"RETIRO".equalsIgnoreCase(
                pedido.getModalidadEntrega())) {

            throw new RuntimeException(
                    "El pedido no corresponde a modalidad RETIRO");
        }

        if (pedido.getNombrePersonaRetira() == null
                || pedido.getNombrePersonaRetira().isBlank()
                || pedido.getRutPersonaRetira() == null
                || pedido.getRutPersonaRetira().isBlank()) {

            throw new RuntimeException(
                    "Debe registrar los datos de la persona que retira antes de finalizar el pedido");
        }

        pedido.setEstado("FINALIZADO");

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido cancelarPedido(Long idPedido) {

        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() ->
                        new RuntimeException("Pedido no encontrado"));

        String estado = pedido.getEstado();

        if ("FINALIZADO".equals(estado)) {
            throw new RuntimeException(
                    "No se puede cancelar un pedido finalizado");
        }

        if ("PAGO_APROBADO".equals(estado)
                || "EN_PREPARACION".equals(estado)
                || "LISTO_RETIRO".equals(estado)
                || "ENVIADO".equals(estado)) {

            throw new RuntimeException(
                    "El pedido ya avanzó a una etapa que no permite cancelación");
        }

        pedido.setEstado("CANCELADO");

        return pedidoRepository.save(pedido);
    }
}