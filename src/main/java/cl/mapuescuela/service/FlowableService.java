package cl.mapuescuela.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class FlowableService {

    private final RestTemplate restTemplate;

    @Value("${flowable.url}")
    private String flowableUrl;

    @Value("${flowable.username}")
    private String flowableUsername;

    @Value("${flowable.password}")
    private String flowablePassword;

    @Value("${flowable.process-definition-key}")
    private String processDefinitionKey;

    public FlowableService() {
        this.restTemplate = new RestTemplate();
    }

    public String iniciarProceso(
            Long idPedido,
            String modalidadEntrega) {

        String url =
                flowableUrl + "/runtime/process-instances";

        HttpHeaders headers = crearHeaders();

        Map<String, Object> body =
                new LinkedHashMap<>();

        body.put(
                "processDefinitionKey",
                processDefinitionKey);

        body.put(
                "businessKey",
                "PEDIDO-" + idPedido);

        List<Map<String, Object>> variables =
                new ArrayList<>();

        Map<String, Object> variableIdPedido =
                new LinkedHashMap<>();

        variableIdPedido.put("name", "idPedido");
        variableIdPedido.put("value", idPedido);

        variables.add(variableIdPedido);

        Map<String, Object> variableModalidad =
                new LinkedHashMap<>();

        variableModalidad.put(
                "name",
                "modalidadEntrega");

        variableModalidad.put(
                "value",
                modalidadEntrega);

        variables.add(variableModalidad);

        body.put("variables", variables);

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(
                        url,
                        entity,
                        Map.class);

        if (response.getBody() == null
                || response.getBody().get("id") == null) {

            throw new RuntimeException(
                    "Flowable no devolvió el identificador de la instancia");
        }

        return response.getBody()
                .get("id")
                .toString();
    }

    public void registrarIdComprobante(
            Long idPedido,
            Long idComprobante) {

        String idInstancia =
                obtenerIdInstanciaActiva(idPedido);

        String urlVariables =
                flowableUrl
                        + "/runtime/process-instances/"
                        + idInstancia
                        + "/variables";

        List<Map<String, Object>> variables =
                new ArrayList<>();

        Map<String, Object> variable =
                new LinkedHashMap<>();

        variable.put(
                "name",
                "idComprobante");

        variable.put(
                "value",
                idComprobante);

        variables.add(variable);

        HttpEntity<List<Map<String, Object>>> entityVariables =
                new HttpEntity<>(
                        variables,
                        crearHeaders());

        restTemplate.exchange(
                urlVariables,
                HttpMethod.PUT,
                entityVariables,
                Void.class);

        System.out.println(
                "[FLOWABLE] idComprobante="
                        + idComprobante
                        + " registrado en PEDIDO-"
                        + idPedido);
    }

    public List<Map<String, Object>> obtenerTareasActivas(
            Long idPedido) {

        String idInstancia =
                obtenerIdInstanciaActiva(idPedido);

        String url =
                UriComponentsBuilder
                        .fromUriString(
                                flowableUrl
                                        + "/runtime/tasks")
                        .queryParam(
                                "processInstanceId",
                                idInstancia)
                        .toUriString();

        HttpEntity<Void> entity =
                new HttpEntity<>(crearHeaders());

        ResponseEntity<Map<String, Object>> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<
                                Map<String, Object>>() {
                        });

        Map<String, Object> body =
                response.getBody();

        List<Map<String, Object>> tareas =
                new ArrayList<>();

        if (body == null
                || !(body.get("data") instanceof List<?>)) {

            return tareas;
        }

        List<?> datos =
                (List<?>) body.get("data");

        for (Object dato : datos) {

            if (dato instanceof Map<?, ?>) {

                Map<?, ?> mapaOriginal =
                        (Map<?, ?>) dato;

                Map<String, Object> tarea =
                        new LinkedHashMap<>();

                for (Map.Entry<?, ?> entry
                        : mapaOriginal.entrySet()) {

                    if (entry.getKey() != null) {
                        tarea.put(
                                entry.getKey().toString(),
                                entry.getValue());
                    }
                }

                tareas.add(tarea);
            }
        }

        return tareas;
    }

    public Map<String, Object> obtenerTareaActivaPorNombre(
            Long idPedido,
            String nombreTarea) {

        List<Map<String, Object>> tareas =
                obtenerTareasActivas(idPedido);

        for (Map<String, Object> tarea : tareas) {

            Object nombre =
                    tarea.get("name");

            if (nombre != null
                    && nombreTarea.equalsIgnoreCase(
                            nombre.toString())) {

                return tarea;
            }
        }

        throw new RuntimeException(
                "No se encontró la tarea activa '"
                        + nombreTarea
                        + "' para el pedido "
                        + idPedido);
    }

    public void completarTarea(
            Long idPedido,
            String nombreTarea) {

        completarTarea(
                idPedido,
                nombreTarea,
                new LinkedHashMap<>());
    }

    public void completarTarea(
            Long idPedido,
            String nombreTarea,
            Map<String, Object> variables) {

        Map<String, Object> tarea =
                obtenerTareaActivaPorNombre(
                        idPedido,
                        nombreTarea);

        Object idTarea =
                tarea.get("id");

        if (idTarea == null) {
            throw new RuntimeException(
                    "Flowable no devolvió el identificador de la tarea");
        }

        String url =
                flowableUrl
                        + "/runtime/tasks/"
                        + idTarea;

        Map<String, Object> body =
                new LinkedHashMap<>();

        body.put("action", "complete");

        if (variables != null
                && !variables.isEmpty()) {

            List<Map<String, Object>> variablesFlowable =
                    new ArrayList<>();

            for (Map.Entry<String, Object> entry
                    : variables.entrySet()) {

                Map<String, Object> variable =
                        new LinkedHashMap<>();

                variable.put(
                        "name",
                        entry.getKey());

                variable.put(
                        "value",
                        entry.getValue());

                variablesFlowable.add(variable);
            }

            body.put(
                    "variables",
                    variablesFlowable);
        }

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(
                        body,
                        crearHeaders());

        restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                Void.class);

        System.out.println(
                "[FLOWABLE] Tarea completada: "
                        + nombreTarea
                        + " | PEDIDO-"
                        + idPedido);
    }

    private String obtenerIdInstanciaActiva(
            Long idPedido) {

        String businessKey =
                "PEDIDO-" + idPedido;

        String urlBusqueda =
                UriComponentsBuilder
                        .fromUriString(
                                flowableUrl
                                        + "/runtime/process-instances")
                        .queryParam(
                                "businessKey",
                                businessKey)
                        .toUriString();

        HttpEntity<Void> entityBusqueda =
                new HttpEntity<>(crearHeaders());

        ResponseEntity<Map<String, Object>> response =
                restTemplate.exchange(
                        urlBusqueda,
                        HttpMethod.GET,
                        entityBusqueda,
                        new ParameterizedTypeReference<
                                Map<String, Object>>() {
                        });

        Map<String, Object> body =
                response.getBody();

        if (body == null
                || !(body.get("data") instanceof List<?>)) {

            throw new RuntimeException(
                    "No fue posible obtener la instancia de Flowable para "
                            + businessKey);
        }

        List<?> instancias =
                (List<?>) body.get("data");

        if (instancias.isEmpty()) {

            throw new RuntimeException(
                    "No existe una instancia activa de Flowable para "
                            + businessKey);
        }

        Object primeraInstancia =
                instancias.get(0);

        if (!(primeraInstancia instanceof Map<?, ?>)) {

            throw new RuntimeException(
                    "Respuesta inválida de Flowable");
        }

        Map<?, ?> instancia =
                (Map<?, ?>) primeraInstancia;

        Object idInstancia =
                instancia.get("id");

        if (idInstancia == null) {

            throw new RuntimeException(
                    "Flowable no devolvió el ID de la instancia");
        }

        return idInstancia.toString();
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
}