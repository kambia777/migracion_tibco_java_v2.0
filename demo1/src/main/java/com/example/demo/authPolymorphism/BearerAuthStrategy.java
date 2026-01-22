package com.example.demo.authPolymorphism;

import org.springframework.http.HttpHeaders;

import com.example.demo.authInterface.AuthenticationStrategy;

public class BearerAuthStrategy implements AuthenticationStrategy {
	
	private final String token;

    public BearerAuthStrategy(String token) {
        this.token = token;
    }

    @Override
    public void apply(HttpHeaders headers) {
        headers.setBearerAuth(token);
    }

}
