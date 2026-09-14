package cl.mapuescuela.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(
                flowableUsername,
                flowablePassword);

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
}