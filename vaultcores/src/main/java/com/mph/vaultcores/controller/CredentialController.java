package com.mph.vaultcores.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mph.vaultcores.service.CredentialService;
import com.mph.vaultcores.service.LegacyCredentialClient;

@RestController
public class CredentialController {

	@Autowired
	private CredentialService credentialService;
	
	@Autowired
	private LegacyCredentialClient client;
	
	@GetMapping("/get-credential")
	public Map<String, String> getCredential(@RequestParam String key){
		String value = credentialService.getCredentialValue(key);
		if(value == null) {
			return Map.of("error", "Credential not found");
		}
		return Map.of("value", value);
	}
	
	@GetMapping("/get-credential-legacy")
	public Map<String, String> getCredentialLegacy(@RequestParam String key){
		String value = client.getCredentialValueByKey(key);
		if(value != null) {
			return Map.of("value", value);
		}else {
			return Map.of("error", "Credential not found via legacy");
		}
	}
}
