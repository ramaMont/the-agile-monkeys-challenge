package com.aifindr.gateway.config.oauth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.oauth")
public record OAuthIssuerProperties(String issuerUrl, String clientId, String clientSecret) {

	public OAuthIssuerProperties {
		clientId = blankToDefault(clientId, "aifindr");
		clientSecret = blankToDefault(clientSecret, "aifindr-dev-secret");
	}

	String canonicalIssuerUrl() {
		String issuer = issuerUrl == null ? "" : issuerUrl.strip();
		if (issuer.endsWith("/")) {
			return issuer.substring(0, issuer.length() - 1);
		}
		return issuer;
	}

	private static String blankToDefault(String value, String fallback) {
		return (value == null || value.isBlank()) ? fallback : value;
	}
}
