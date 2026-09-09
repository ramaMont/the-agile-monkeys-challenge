package com.aifindr.gateway.reservationProposals;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReservationProposalService {

	private final ReservationProposalRepository proposals;

	public ReservationProposalService(ReservationProposalRepository proposals) {
		this.proposals = proposals;
	}

	@Transactional
	public ReservationProposalResponse propose(
			String customerName,
			String branch,
			String purpose,
			String dateTime) {
		ReservationProposal saved = proposals.save(
				ReservationProposal.pending(customerName, branch, purpose, dateTime)
		);
		return saved.toResponse();
	}

	@Transactional(readOnly = true)
	public List<ReservationProposalResponse> list() {
		return proposals.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
				.stream()
				.map(ReservationProposal::toResponse)
				.toList();
	}
}
