package cl.mapuescuela.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.mapuescuela.dto.RegistrarDespachoRequest;
import cl.mapuescuela.model.Despacho;
import cl.mapuescuela.model.Pedido;
import cl.mapuescuela.repository.DespachoRepository;
import cl.mapuescuela.repository.PedidoRepository;

@Service
public class DespachoService {

    private final DespachoRepository despachoRepository;
    private final PedidoRepository pedidoRepository;

    public DespachoService(
            DespachoRepository despachoRepository,
            PedidoRepository pedidoRepository) {

        this.despachoRepository = despachoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional
    public Despacho registrarEnvio(
            Long idPedido,
            RegistrarDespachoRequest request) {

        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() ->
                        new RuntimeException("Pedido no encontrado"));

        if (!"EN_PREPARACION".equals(pedido.getEstado())) {
            throw new RuntimeException(
                    "El pedido debe estar en preparación");
        }

        if (!"DESPACHO".equalsIgnoreCase(pedido.getModalidadEntrega())) {
            throw new RuntimeException(
                    "El pedido no corresponde a modalidad DESPACHO");
        }

        Despacho despacho = new Despacho();
        despacho.setPedido(pedido);
        despacho.setTipoEntrega("DESPACHO");
        despacho.setEmpresaTransporte(request.getEmpresaTransporte());
        despacho.setNumeroSeguimiento(request.getNumeroSeguimiento());
        despacho.setFechaEnvio(LocalDateTime.now());

        despacho = despachoRepository.save(despacho);

        pedido.setEstado("ENVIADO");
        pedidoRepository.save(pedido);

        return despacho;
    }

    @Transactional
    public Despacho registrarEntrega(Long idPedido) {

        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() ->
                        new RuntimeException("Pedido no encontrado"));

        if (!"ENVIADO".equals(pedido.getEstado())) {
            throw new RuntimeException(
                    "El pedido debe estar en estado ENVIADO");
        }

        Despacho despacho = despachoRepository.findAll()
                .stream()
                .filter(d ->
                        d.getPedido()
                         .getIdPedido()
                         .equals(idPedido))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Despacho no encontrado"));

        despacho.setFechaEntrega(LocalDateTime.now());
        despachoRepository.save(despacho);

        pedido.setEstado("FINALIZADO");
        pedidoRepository.save(pedido);

        return despacho;
    }
}