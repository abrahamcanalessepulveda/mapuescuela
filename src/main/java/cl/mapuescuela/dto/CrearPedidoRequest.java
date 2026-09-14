package cl.mapuescuela.dto;

import java.util.List;

public class CrearPedidoRequest {

    private Long idCliente;
    private String modalidadEntrega;
    private List<ItemPedidoRequest> productos;

    public CrearPedidoRequest() {
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public String getModalidadEntrega() {
        return modalidadEntrega;
    }

    public void setModalidadEntrega(String modalidadEntrega) {
        this.modalidadEntrega = modalidadEntrega;
    }

    public List<ItemPedidoRequest> getProductos() {
        return productos;
    }

    public void setProductos(List<ItemPedidoRequest> productos) {
        this.productos = productos;
    }
}