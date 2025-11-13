package com.example.demo.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.entity.Cliente;

@Service
public class ClienteServiceApi {
	private final RestTemplate restTemplate;

    public ClienteServiceApi() {
        this.restTemplate = new RestTemplate();
    }

    public Cliente[] obtenerTodosClientes(String usuario, String password) {
        // Preparar cabeceras
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Crear Basic Auth
        String auth = usuario + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + new String(encodedAuth);
        headers.set("Authorization", authHeader);

        // Crear entidad con headers
        HttpEntity<String> request = new HttpEntity<>(headers);

        // Hacer la petición GET
        ResponseEntity<Cliente[]> response = restTemplate.exchange(
                "http://localhost:9090/clientes",
                org.springframework.http.HttpMethod.GET,
                request,
                Cliente[].class
        );

        // Imprimir los clientes
        for (Cliente c : response.getBody()) {
            System.out.println("ID: " + c.getId() + ", Código: " + c.getCodigo());
        }

        return response.getBody();
    }

}
