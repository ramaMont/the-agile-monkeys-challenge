package com.aifindr.gateway.reservationProposals;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/reservation-proposals")
public class ReservationProposalController {

	private final ReservationProposalService proposals;

	public ReservationProposalController(ReservationProposalService proposals) {
		this.proposals = proposals;
	}

	@GetMapping
	public List<ReservationProposalResponse> list() {
		return proposals.list();
	}
}
