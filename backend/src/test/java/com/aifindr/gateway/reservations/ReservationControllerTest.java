package com.aifindr.gateway.reservations;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
@Import(ReservationService.class)
class ReservationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void listReturnsEmptyArray() throws Exception {
		mockMvc.perform(get("/v1/actions/reservations"))
				.andExpect(status().isOk())
				.andExpect(content().json("[]"));
	}
}
