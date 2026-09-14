package com.soap.ws;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

@WebService
public class MiServicioSOAP {

    @WebMethod
    public String registrarCliente(String nombre, String email, String telefono) {
        // Lógica para registrar cliente
        System.out.println("Cliente registrado: " + nombre);
        return "Cliente " + nombre + " registrado con éxito";
    }

    @WebMethod
    public String generarPedido(int productoId, int cantidad, String clienteEmail) {
        // Lógica para generar pedido
        System.out.println("Pedido generado para: " + clienteEmail);
        return "Pedido #" + System.currentTimeMillis() + " generado";
    }

    @WebMethod
    public String procesarPago(String pedidoId, String comprobante) {
        // Lógica para procesar pago
        System.out.println("Pago procesado para pedido: " + pedidoId);
        return "Pago aprobado";
    }

    @WebMethod
    public String actualizarEstadoPedido(String pedidoId, String estado) {
        // Lógica para actualizar estado
        System.out.println("Pedido " + pedidoId + " ahora en estado: " + estado);
        return "Estado actualizado a: " + estado;
    }
}