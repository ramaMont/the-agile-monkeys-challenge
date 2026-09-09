package com.aifindr.gateway.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthLoginIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void loginReturnsToken() throws Exception {
		mockMvc.perform(post("/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"user\",\"password\":\"password\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token", notNullValue()));
	}

	@Test
	void loginRejectsBadPassword() throws Exception {
		mockMvc.perform(post("/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"user\",\"password\":\"wrong\"}"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void reservationProposalsRequireBearerToken() throws Exception {
		mockMvc.perform(get("/v1/reservation-proposals"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void reservationProposalsAcceptValidToken() throws Exception {
		MvcResult login = mockMvc.perform(post("/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"user\",\"password\":\"password\"}"))
				.andExpect(status().isOk())
				.andReturn();

		String body = login.getResponse().getContentAsString();
		String token = body.replaceAll(".*\"token\"\\s*:\\s*\"([^\"]+)\".*", "$1");

		mockMvc.perform(get("/v1/reservation-proposals")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());
	}
}
