package com.aifindr.gateway.config.oauth;

import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

final class RequestRedirectClientRepository implements RegisteredClientRepository {

	private final RegisteredClientRepository delegate;

	RequestRedirectClientRepository(RegisteredClientRepository delegate) {
		this.delegate = delegate;
	}

	@Override
	public void save(RegisteredClient registeredClient) {
		delegate.save(registeredClient);
	}

	@Override
	public RegisteredClient findById(String id) {
		return withRequestRedirect(delegate.findById(id));
	}

	@Override
	public RegisteredClient findByClientId(String clientId) {
		return withRequestRedirect(delegate.findByClientId(clientId));
	}

	private static RegisteredClient withRequestRedirect(RegisteredClient client) {
		if (client == null) {
			return null;
		}
		String redirectUri = currentRedirectUri();
		if (!RedirectAllowlist.isAllowed(redirectUri) || client.getRedirectUris().contains(redirectUri)) {
			return client;
		}
		return RegisteredClient.from(client).redirectUri(redirectUri).build();
	}

	private static String currentRedirectUri() {
		if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs)) {
			return null;
		}
		return attrs.getRequest().getParameter("redirect_uri");
	}
}
