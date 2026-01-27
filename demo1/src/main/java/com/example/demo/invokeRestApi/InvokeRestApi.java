package com.example.demo.invokeRestApi;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.example.demo.authInterface.AuthenticationStrategy;

public class InvokeRestApi {
	
	private final RestTemplate restTemplate;

    public InvokeRestApi(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    //Metodo Invoke Rest Api
    public <T> T execute(
            String url,
            HttpMethod method,
            Object body,
            AuthenticationStrategy authenticationStrategy,
            Class<T> responseType
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // POLIMORFISMO DE AUTENTICACIÓN
        if (authenticationStrategy != null) {
            authenticationStrategy.apply(headers);
        }

        HttpEntity<Object> request = new HttpEntity<>(body, headers);

        ResponseEntity<T> response = restTemplate.exchange(
                url,
                method,
                request,
                responseType
        );

        return response.getBody();
    }

}
