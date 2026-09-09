package com.aifindr.gateway.reservationProposals;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

	@Transactional(readOnly = true)
	public ReservationProposalResponse get(String id) {
		return proposals.findById(id)
				.map(ReservationProposal::toResponse)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	@Transactional
	public ReservationProposalResponse updateStatus(String id, ReservationProposalStatus status) {
		ReservationProposal proposal = proposals.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		try {
			proposal.applyStatus(status);
		}
		catch (IllegalStateException ex) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage(), ex);
		}
		catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
		}
		return proposals.save(proposal).toResponse();
	}
}
