package com.example.demo.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


import com.example.demo.dto.EstadoCrediticioDTO;


public class EstadoCrediticioServiceApi {

	private final RestTemplate restTemplate;

	// URL de la API externa configurable (Docker-friendly)
	@Value("${api.terceros.url}")
	private String apiBaseUrl;

	public EstadoCrediticioServiceApi() {
		this.restTemplate = new RestTemplate();
	}

	public EstadoCrediticioDTO esDeudor(String usuario, String password, Long codigoCliente) {
		try {
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

			// --- Construcción de URL ---
			String url = "http://host.docker.internal:9090" + "/api/v1/estados-crediticios?codigoCliente=" + codigoCliente;
			System.out.println("🔗 Consumiento API externa: " + url);
			// Hacer la petición GET
			ResponseEntity<EstadoCrediticioDTO> response = restTemplate.exchange(
					url,
					org.springframework.http.HttpMethod.GET,
					request,
					EstadoCrediticioDTO.class
					);

			// Imprimir los clientes
			//for (EstadoCrediticioDTO c : response.getBody()) {
			//    System.out.println("ID: " + c.getId() + ", Código: " + c.getCodigo());
			//}

			return response.getBody();
		} catch (Exception ex) {
			System.err.println("❌ Error llamando a API de terceros: " + ex.getMessage());
			return null; // O lanzar excepción personalizada
		}
	}


}
