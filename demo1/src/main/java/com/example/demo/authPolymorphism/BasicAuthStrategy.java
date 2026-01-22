package com.example.demo.authPolymorphism;

import org.springframework.http.HttpHeaders;

import com.example.demo.authInterface.AuthenticationStrategy;

public class BasicAuthStrategy implements AuthenticationStrategy {
	
	private final String username;
    private final String password;

    public BasicAuthStrategy(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void apply(HttpHeaders headers) {
        headers.setBasicAuth(username, password);
    }

}
