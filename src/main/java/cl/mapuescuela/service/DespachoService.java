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
    public Despacho registrarDatosEnvio(
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

        if (request.getEmpresaTransporte() == null
                || request.getEmpresaTransporte().isBlank()) {
            throw new RuntimeException(
                    "Debe ingresar la empresa de transporte");
        }

        if (request.getNumeroSeguimiento() == null
                || request.getNumeroSeguimiento().isBlank()) {
            throw new RuntimeException(
                    "Debe ingresar el número de seguimiento");
        }

        if (request.getFechaEnvio() == null) {
            throw new RuntimeException(
                    "Debe ingresar la fecha de envío");
        }

        Despacho despacho = despachoRepository
                .findByPedidoIdPedido(idPedido)
                .orElseGet(Despacho::new);

        despacho.setPedido(pedido);
        despacho.setTipoEntrega("DESPACHO");
        despacho.setEmpresaTransporte(
                request.getEmpresaTransporte().trim());
        despacho.setNumeroSeguimiento(
                request.getNumeroSeguimiento().trim());
        despacho.setFechaEnvio(request.getFechaEnvio());

        return despachoRepository.save(despacho);
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

        Despacho despacho = despachoRepository
                .findByPedidoIdPedido(idPedido)
                .orElse(null);

        if (despacho == null) {

            if (request.getNumeroSeguimiento() == null
                    || request.getNumeroSeguimiento().isBlank()) {
                throw new RuntimeException(
                        "No existen datos de despacho registrados");
            }

            despacho = new Despacho();
            despacho.setPedido(pedido);
            despacho.setTipoEntrega("DESPACHO");
            despacho.setEmpresaTransporte(
                    request.getEmpresaTransporte());
            despacho.setNumeroSeguimiento(
                    request.getNumeroSeguimiento());
            despacho.setFechaEnvio(
                    request.getFechaEnvio() != null
                            ? request.getFechaEnvio()
                            : LocalDateTime.now());

        } else {

            if (despacho.getEmpresaTransporte() == null
                    || despacho.getEmpresaTransporte().isBlank()) {
                throw new RuntimeException(
                        "El despacho no tiene empresa de transporte registrada");
            }

            if (despacho.getNumeroSeguimiento() == null
                    || despacho.getNumeroSeguimiento().isBlank()) {
                throw new RuntimeException(
                        "El despacho no tiene número de seguimiento registrado");
            }

            if (despacho.getFechaEnvio() == null) {
                throw new RuntimeException(
                        "El despacho no tiene fecha de envío registrada");
            }
        }

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

        Despacho despacho = despachoRepository
                .findByPedidoIdPedido(idPedido)
                .orElseThrow(() ->
                        new RuntimeException("Despacho no encontrado"));

        despacho.setFechaEntrega(LocalDateTime.now());
        despachoRepository.save(despacho);

        pedido.setEstado("FINALIZADO");
        pedidoRepository.save(pedido);

        return despacho;
    }
}