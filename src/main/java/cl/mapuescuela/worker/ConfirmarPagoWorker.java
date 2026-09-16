package cl.mapuescuela.worker;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import cl.mapuescuela.service.ComprobantePagoService;

@Component
public class ConfirmarPagoWorker {

    private static final String TOPIC = "confirmarPago";
    private static final String WORKER_ID = "mapuescuela-worker";

    private final ComprobantePagoService comprobantePagoService;
    private final RestTemplate restTemplate;

    @Value("${flowable.url}")
    private String flowableUrl;

    @Value("${flowable.username}")
    private String flowableUsername;

    @Value("${flowable.password}")
    private String flowablePassword;

    public ConfirmarPagoWorker(
            ComprobantePagoService comprobantePagoService) {

        this.comprobantePagoService = comprobantePagoService;
        this.restTemplate = new RestTemplate();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void mostrarInicio() {

        System.out.println(
                "[EXTERNAL WORKER] Iniciado. Topic: "
                        + TOPIC);
    }

    @Scheduled(fixedDelay = 5000)
    public void buscarTrabajos() {

        try {

            String url =
                    obtenerExternalJobUrl()
                            + "/acquire/jobs";

            HttpHeaders headers = crearHeaders();

            Map<String, Object> body =
                    new LinkedHashMap<>();

            body.put("topic", TOPIC);
            body.put("lockDuration", "PT5M");
            body.put("workerId", WORKER_ID);
            body.put("numberOfTasks", 1);

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(body, headers);

            List<?> trabajos =
                    restTemplate.postForObject(
                            url,
                            entity,
                            List.class);

            if (trabajos == null
                    || trabajos.isEmpty()) {

                return;
            }

            for (Object trabajo : trabajos) {

                if (trabajo instanceof Map<?, ?> mapa) {
                    procesarTrabajo(mapa);
                }
            }

        } catch (Exception e) {

            System.err.println(
                    "[EXTERNAL WORKER] Error consultando Flowable: "
                            + e.getMessage());
        }
    }

    private void procesarTrabajo(
            Map<?, ?> trabajo) {

        Object idJobObject = trabajo.get("id");

        if (idJobObject == null) {

            System.err.println(
                    "[EXTERNAL WORKER] Flowable devolvio un trabajo sin ID.");

            return;
        }

        String idJob = idJobObject.toString();

        try {

            Long idComprobante =
                    obtenerIdComprobante(trabajo);

            System.out.println(
                    "[EXTERNAL WORKER] Trabajo adquirido: "
                            + idJob);

            System.out.println(
                    "[EXTERNAL WORKER] Topic: "
                            + TOPIC
                            + " | idComprobante: "
                            + idComprobante);

            comprobantePagoService.validarComprobante(
                    idComprobante,
                    "APROBADO",
                    "Pago aprobado mediante External Worker");

            completarTrabajo(idJob);

            System.out.println(
                    "[EXTERNAL WORKER] Comprobante "
                            + idComprobante
                            + " aprobado correctamente.");

            System.out.println(
                    "[EXTERNAL WORKER] Trabajo completado en Flowable: "
                            + idJob);

        } catch (Exception e) {

            System.err.println(
                    "[EXTERNAL WORKER] Error procesando trabajo "
                            + idJob
                            + ": "
                            + e.getMessage());
        }
    }

    private Long obtenerIdComprobante(
            Map<?, ?> trabajo) {

        Object variablesObject =
                trabajo.get("variables");

        if (!(variablesObject instanceof List<?> variables)) {

            throw new RuntimeException(
                    "El trabajo no contiene variables");
        }

        for (Object variableObject : variables) {

            if (variableObject instanceof Map<?, ?> variable) {

                Object nombre = variable.get("name");

                if ("idComprobante".equals(
                        String.valueOf(nombre))) {

                    Object valor = variable.get("value");

                    if (valor == null) {

                        throw new RuntimeException(
                                "idComprobante no tiene valor");
                    }

                    return Long.valueOf(
                            valor.toString());
                }
            }
        }

        throw new RuntimeException(
                "No se encontro la variable idComprobante");
    }

    private void completarTrabajo(
            String idJob) {

        String url =
                obtenerExternalJobUrl()
                        + "/acquire/jobs/"
                        + idJob
                        + "/complete";

        HttpHeaders headers = crearHeaders();

        Map<String, Object> body =
                new LinkedHashMap<>();

        body.put("workerId", WORKER_ID);

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(body, headers);

        restTemplate.postForEntity(
                url,
                entity,
                Void.class);
    }

    private HttpHeaders crearHeaders() {

        HttpHeaders headers =
                new HttpHeaders();

        headers.setContentType(
                MediaType.APPLICATION_JSON);

        headers.setBasicAuth(
                flowableUsername,
                flowablePassword);

        return headers;
    }

    private String obtenerExternalJobUrl() {

        String baseUrl = flowableUrl;

        if (baseUrl.endsWith("/service")) {

            baseUrl = baseUrl.substring(
                    0,
                    baseUrl.length()
                            - "/service".length());
        }

        return baseUrl
                + "/external-job-api";
    }
}