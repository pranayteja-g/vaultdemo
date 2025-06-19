package com.mph.vaultcores.service;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.mph.vaultcores.model.Credential;

@Component
public class LegacyCredentialClient {

	private final RestTemplate restTemplate;

	public LegacyCredentialClient(RestTemplate restTemplate) {
		super();
		this.restTemplate = restTemplate;
	}

	public Credential[] fetchCredentials() {
		String url = "http://localhost:8081/credentials";
		return restTemplate.getForObject(url, Credential[].class);
	}

	public String getCredentialValueByKey(String key) {
		Credential[] credentials = fetchCredentials();
		if (credentials != null) {
			for (Credential c : credentials) {
				if (c.getKey().equalsIgnoreCase(key)) {
					return c.getValue();
				}
			}
		}
		return null;
	}
}
