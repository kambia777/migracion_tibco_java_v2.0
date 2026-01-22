package com.example.demo.authInterface;

import org.springframework.http.HttpHeaders;

public interface AuthenticationStrategy {
	void apply(HttpHeaders headers);
}
