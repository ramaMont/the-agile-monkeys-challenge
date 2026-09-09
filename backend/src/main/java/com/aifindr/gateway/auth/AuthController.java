package com.aifindr.gateway.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final JwtTokenService tokens;

	public AuthController(AuthenticationManager authenticationManager, JwtTokenService tokens) {
		this.authenticationManager = authenticationManager;
		this.tokens = tokens;
	}

	@PostMapping("/login")
	public LoginResponse login(@RequestBody LoginRequest request) {
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.username(), request.password()));
		}
		catch (AuthenticationException ex) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
		}
		return new LoginResponse(tokens.issueToken(request.username()));
	}
}
