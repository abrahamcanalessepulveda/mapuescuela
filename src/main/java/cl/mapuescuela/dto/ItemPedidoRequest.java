package cl.mapuescuela.dto;

public class ItemPedidoRequest {

    private Long idProducto;
    private Integer cantidad;

    public ItemPedidoRequest() {
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}