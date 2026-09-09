package com.aifindr.gateway.auth;

import com.aifindr.gateway.config.oauth.OAuthIssuerProperties;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class JwtTokenService {

	private final JwtEncoder jwtEncoder;
	private final OAuthIssuerProperties oauth;

	public JwtTokenService(JwtEncoder jwtEncoder, OAuthIssuerProperties oauth) {
		this.jwtEncoder = jwtEncoder;
		this.oauth = oauth;
	}

	public String issueToken(String username) {
		Instant now = Instant.now();
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer(oauth.canonicalIssuerUrl())
				.issuedAt(now)
				.expiresAt(now.plus(1, ChronoUnit.HOURS))
				.subject(username)
				.build();
		JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256).build();
		return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
	}
}
