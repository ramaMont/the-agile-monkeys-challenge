package com.aifindr.gateway.reservationProposals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
	void listReturnsEmptyPage() throws Exception {
		when(proposals.list(0, 10)).thenReturn(new ReservationProposalPageResponse(
				List.of(),
				0,
				10,
				0,
				0
		));

		mockMvc.perform(get("/v1/reservation-proposals"))
				.andExpect(status().isOk())
				.andExpect(content().json("""
						{
						  "content": [],
						  "page": 0,
						  "size": 10,
						  "totalElements": 0,
						  "totalPages": 0
						}
						"""));
	}

	@Test
	void patchUpdatesStatus() throws Exception {
		when(proposals.updateStatus("abc", ReservationProposalStatus.APPROVED))
				.thenReturn(new ReservationProposalResponse(
						"abc",
						ReservationProposalStatus.APPROVED,
						"Ada Lovelace",
						"Madrid Centro",
						"Mortgage advice",
						"2026-09-20T10:00"
				));

		mockMvc.perform(patch("/v1/reservation-proposals/abc")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"status\":\"APPROVED\"}"))
				.andExpect(status().isOk())
				.andExpect(content().json("""
						{
						  "id": "abc",
						  "status": "APPROVED",
						  "customerName": "Ada Lovelace",
						  "branch": "Madrid Centro",
						  "purpose": "Mortgage advice",
						  "dateTime": "2026-09-20T10:00"
						}
						"""));
	}
}
