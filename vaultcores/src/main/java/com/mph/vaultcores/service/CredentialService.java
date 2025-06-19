package com.mph.vaultcores.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.mph.vaultcores.model.Credential;

import jakarta.annotation.PostConstruct;

@Service
public class CredentialService {

	private final WebClient webClient = WebClient.create("http://localhost:8081");
	private final Map<String, String> credentialMap = new ConcurrentHashMap<>();
	
	@PostConstruct
	public void loadCredentialsFromVault() {
		System.out.println("Fetching credentials from core Java vault Server...");
		Credential[] credentials = webClient.get()
				.uri("/credentials")
				.retrieve()
				.bodyToMono(Credential[].class)
				.block();
		
		if(credentials != null) {
			for(Credential credential : credentials) {
				credentialMap.put(credential.getKey(), credential.getValue());
			}
		System.out.println("loaded credentials: " + credentialMap);
		} else {
			System.out.println("Failed to load credentials!");
		}
	}
	
	public String getCredentialValue(String key) {
		return credentialMap.get(key);
	}
}
