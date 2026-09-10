package com.aifindr.gateway.reservationProposals;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.function.Supplier;

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
		ReservationProposalValidator.NormalizedProposal input = requireValid(
				() -> ReservationProposalValidator.requireProposal(customerName, branch, purpose, dateTime)
		);
		ReservationProposal saved = proposals.save(
				ReservationProposal.pending(input.customerName(), input.branch(), input.purpose(), input.dateTime())
		);
		return saved.toResponse();
	}

	@Transactional(readOnly = true)
	public ReservationProposalPageResponse list(int page, int size) {
		requireValid(() -> ReservationProposalValidator.requirePage(page, size));
		Page<ReservationProposal> result = proposals.findAll(
				PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
		);
		return new ReservationProposalPageResponse(
				result.map(ReservationProposal::toResponse).getContent(),
				result.getNumber(),
				result.getSize(),
				result.getTotalElements(),
				result.getTotalPages()
		);
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

	private static void requireValid(Runnable check) {
		requireValid(() -> {
			check.run();
			return null;
		});
	}

	private static <T> T requireValid(Supplier<T> check) {
		try {
			return check.get();
		}
		catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
		}
	}
}
