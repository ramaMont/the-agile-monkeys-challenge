package com.aifindr.gateway.config.oauth;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationContext;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationException;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationToken;

import java.util.function.Consumer;

final class AifindrAuthorizeValidator
		implements Consumer<OAuth2AuthorizationCodeRequestAuthenticationContext> {

	@Override
	public void accept(OAuth2AuthorizationCodeRequestAuthenticationContext context) {
		var authentication = (OAuth2AuthorizationCodeRequestAuthenticationToken) context.getAuthentication();
		String redirectUri = authentication.getRedirectUri();
		if (redirectUri == null || redirectUri.isBlank()) {
			return;
		}
		if (!RedirectAllowlist.isAllowed(redirectUri)) {
			OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST, "Invalid redirect_uri", null);
			throw new OAuth2AuthorizationCodeRequestAuthenticationException(error, authentication);
		}
	}
}
