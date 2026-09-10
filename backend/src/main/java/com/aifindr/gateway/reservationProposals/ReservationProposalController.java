package com.aifindr.gateway.reservationProposals;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/reservation-proposals")
public class ReservationProposalController {

	private final ReservationProposalService proposals;

	public ReservationProposalController(ReservationProposalService proposals) {
		this.proposals = proposals;
	}

	@GetMapping
	public ReservationProposalPageResponse list(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		return proposals.list(page, size);
	}

	@PatchMapping("/{id}")
	public ReservationProposalResponse update(
			@PathVariable String id,
			@RequestBody ReservationProposalUpdateRequest request) {
		return proposals.updateStatus(id, request.status());
	}
}
