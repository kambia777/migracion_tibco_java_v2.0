package com.example.demo.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.demo.authInterface.AuthenticationStrategy;
import com.example.demo.dto.EstadoCrediticioDTO;
import com.example.demo.invokeRestApi.InvokeRestApi;

@Component
public class EstadoCrediticioServiceApi {

	private final RestTemplate restTemplate1;

	// URL de la API externa configurable (Docker-friendly)
	@Value("${api.terceros.url}")
	private String apiBaseUrl;

	public EstadoCrediticioServiceApi() {
		this.restTemplate1 = new RestTemplate();
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
			String url = "http://localhost:9090" + "/api/v1/estados-crediticios?codigoCliente=" + codigoCliente;
			System.out.println("🔗 Consumiento API externa: " + url);
			// Hacer la petición GET
			ResponseEntity<EstadoCrediticioDTO> response = restTemplate1.exchange(
					url,
					org.springframework.http.HttpMethod.GET,
					request,
					EstadoCrediticioDTO.class
					);

			return response.getBody();
		} catch (Exception ex) {
			System.err.println("❌ Error llamando a API de terceros: " + ex.getMessage());
			return null; // O lanzar excepción personalizada
		}
	}

	
	public EstadoCrediticioDTO esDeudorNuevo(AuthenticationStrategy authenticationStrategy,
            Long codigoCliente) {
		try {
			HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 🔥 POLIMORFISMO AQUÍ
            authenticationStrategy.apply(headers);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            String url = "http://localhost:9090"
                    + "/api/v1/estados-crediticios?codigoCliente="
                    + codigoCliente;

            System.out.println("🔗 Consumiento API externa: " + url);
            ResponseEntity<EstadoCrediticioDTO> response =
                    restTemplate1.exchange(
                            url,
                            HttpMethod.GET,
                            request,
                            EstadoCrediticioDTO.class
                    );

            return response.getBody();

        } catch (Exception ex) {
            System.err.println("❌ Error llamando a API de terceros: " + ex.getMessage());
            return null;
        }
	}
	
	public EstadoCrediticioDTO esDeudorGenerico(
	        AuthenticationStrategy authenticationStrategy,
	        Long codigoCliente
	) {
	    try {
	        String url = "http://localhost:9090"
	                + "/api/v1/estados-crediticios?codigoCliente="
	                + codigoCliente;

	        System.out.println("🔗 Consumiento API externa: " + url);
	        //Pieza invokeRestApi
	        InvokeRestApi invokeRestApi =
	                new InvokeRestApi(new RestTemplate());
	        //configuración de la pieza invokeRestApi
	        EstadoCrediticioDTO dto =
	                invokeRestApi.execute(
	                        url,
	                        HttpMethod.GET,
	                        null,
	                        authenticationStrategy,
	                        EstadoCrediticioDTO.class
	                );


	        return dto;

	    } catch (Exception ex) {
	        System.err.println("❌ Error llamando a API de terceros: " + ex.getMessage());
	        return null;
	    }
	}


}
