package cl.mapuescuela.dto;

public class ValidarComprobanteRequest {

    private String resultado;
    private String observacion;

    public ValidarComprobanteRequest() {
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}