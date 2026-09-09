package com.aifindr.gateway.reservationProposals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationProposalController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReservationProposalControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ReservationProposalService proposals;

	@Test
	void listReturnsEmptyArray() throws Exception {
		when(proposals.list()).thenReturn(List.of());

		mockMvc.perform(get("/v1/reservation-proposals"))
				.andExpect(status().isOk())
				.andExpect(content().json("[]"));
	}
}
