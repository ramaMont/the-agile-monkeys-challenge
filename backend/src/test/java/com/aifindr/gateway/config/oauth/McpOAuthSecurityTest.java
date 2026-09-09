package com.aifindr.gateway.config.oauth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class McpOAuthSecurityTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void healthStaysPublic() throws Exception {
		mockMvc.perform(get("/health"))
				.andExpect(status().isOk());
	}

	@Test
	void reservationProposalsRequireAuth() throws Exception {
		mockMvc.perform(get("/v1/reservation-proposals"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void mcpWithoutTokenReturnsBearerChallenge() throws Exception {
		mockMvc.perform(post("/mcp")
						.contentType("application/json")
						.content("{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"initialize\",\"params\":{}}"))
				.andExpect(status().isUnauthorized())
				.andExpect(header().string("WWW-Authenticate", containsString("Bearer")));
	}

	@Test
	void mcpSseGetWithoutTokenFailsFast() throws Exception {
		mockMvc.perform(get("/mcp").accept("text/event-stream"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void authorizationServerMetadataIsPublic() throws Exception {
		mockMvc.perform(get("/.well-known/oauth-authorization-server"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.issuer").value("http://localhost:8080"))
				.andExpect(jsonPath("$.token_endpoint_auth_methods_supported[0]").value("client_secret_basic"))
				.andExpect(jsonPath("$.token_endpoint_auth_methods_supported[1]").value("client_secret_post"));
	}
}
