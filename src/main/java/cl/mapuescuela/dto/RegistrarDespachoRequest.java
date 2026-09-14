package cl.mapuescuela.dto;

public class RegistrarDespachoRequest {

    private String empresaTransporte;
    private String numeroSeguimiento;

    public RegistrarDespachoRequest() {
    }

    public String getEmpresaTransporte() {
        return empresaTransporte;
    }

    public void setEmpresaTransporte(String empresaTransporte) {
        this.empresaTransporte = empresaTransporte;
    }

    public String getNumeroSeguimiento() {
        return numeroSeguimiento;
    }

    public void setNumeroSeguimiento(String numeroSeguimiento) {
        this.numeroSeguimiento = numeroSeguimiento;
    }
}