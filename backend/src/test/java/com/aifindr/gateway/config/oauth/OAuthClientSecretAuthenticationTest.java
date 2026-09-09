package com.aifindr.gateway.config.oauth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OAuthClientSecretAuthenticationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	void noopClientSecretStillMatchesWithDelegatingEncoder() {
		assertThat(passwordEncoder.matches("aifindr-dev-secret", "{noop}aifindr-dev-secret")).isTrue();
	}

	@Test
	void tokenEndpointRejectsBadClientSecret() throws Exception {
		String basic = Base64.getEncoder()
				.encodeToString("aifindr:wrong-secret".getBytes(StandardCharsets.UTF_8));

		mockMvc.perform(post("/oauth2/token")
						.header(HttpHeaders.AUTHORIZATION, "Basic " + basic)
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("grant_type", "authorization_code")
						.param("code", "not-a-real-code")
						.param("redirect_uri", "https://api-dev.saas.aifindr.ai/oauth/callback"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void tokenEndpointAcceptsClientAuthBeforeCodeValidation() throws Exception {
		String basic = Base64.getEncoder()
				.encodeToString("aifindr:aifindr-dev-secret".getBytes(StandardCharsets.UTF_8));

		// Valid client auth should not return 401 (invalid_client). Bad/missing code → 400.
		mockMvc.perform(post("/oauth2/token")
						.header(HttpHeaders.AUTHORIZATION, "Basic " + basic)
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("grant_type", "authorization_code")
						.param("code", "not-a-real-code")
						.param("redirect_uri", "https://api-dev.saas.aifindr.ai/oauth/callback")
						.param("code_verifier", "verifier-at-least-43-characters-long-xxxxxxxxxx"))
				.andExpect(status().isBadRequest());
	}
}
