package com.aifindr.gateway.config.oauth;

import java.net.URI;

final class RedirectAllowlist {

	private RedirectAllowlist() {
	}

	static boolean isAllowed(String redirectUri) {
		if (redirectUri == null || redirectUri.isBlank()) {
			return false;
		}
		URI uri;
		try {
			uri = URI.create(redirectUri);
		}
		catch (IllegalArgumentException ex) {
			return false;
		}
		if (uri.getFragment() != null || uri.getHost() == null) {
			return false;
		}
		String host = uri.getHost().toLowerCase();
		boolean aifindr = host.equals("aifindr.ai") || host.endsWith(".aifindr.ai");
		boolean loopback = host.equals("localhost") || host.equals("127.0.0.1");
		if (aifindr) {
			return "https".equalsIgnoreCase(uri.getScheme());
		}
		return loopback && ("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()));
	}
}
