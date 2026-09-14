package com.soap.ws;

import jakarta.xml.ws.Endpoint;

public class publicador {
    public static void main(String[] args) {
        String url = "http://localhost:8088/ws/saludo";
        System.out.println("Servicio SOAP corriendo en: " + url);
        Endpoint.publish(url, new MiServicioSOAP());
        System.out.println("¡Listo para recibir peticiones!");
    }
}