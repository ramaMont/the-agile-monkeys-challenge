package com.aifindr.gateway.config.oauth;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;

@Configuration
@EnableConfigurationProperties(OAuthIssuerProperties.class)
public class JwkConfig {

	@Bean
	JWKSource<SecurityContext> jwkSource(OAuthIssuerProperties oauth) {
		return new ImmutableJWKSet<>(new JWKSet(RsaKeyLoader.loadOrGenerate(
				oauth.rsaPrivateKey(),
				oauth.jwkKeyId())));
	}

	@Bean
	JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource, OAuthIssuerProperties oauth) {
		NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSource(jwkSource).build();
		decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(oauth.canonicalIssuerUrl()));
		return decoder;
	}

	@Bean
	JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
		return new NimbusJwtEncoder(jwkSource);
	}

	@Bean
	AuthorizationServerSettings authorizationServerSettings(OAuthIssuerProperties oauth) {
		return AuthorizationServerSettings.builder()
				.issuer(oauth.canonicalIssuerUrl())
				.build();
	}
}
