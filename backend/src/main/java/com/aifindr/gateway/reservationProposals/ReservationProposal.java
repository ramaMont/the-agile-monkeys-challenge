package com.aifindr.gateway.reservationProposals;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reservation_proposals")
public class ReservationProposal {

	@Id
	private String id;
	@Enumerated(EnumType.STRING)
	private ReservationProposalStatus status;
	private String customerName;
	private String branch;
	private String purpose;
	private String dateTime;
	private Instant createdAt;

	protected ReservationProposal() {
	}

	static ReservationProposal pending(
			String customerName,
			String branch,
			String purpose,
			String dateTime) {
		ReservationProposal proposal = new ReservationProposal();
		proposal.id = UUID.randomUUID().toString();
		proposal.status = ReservationProposalStatus.PENDING;
		proposal.customerName = customerName;
		proposal.branch = branch;
		proposal.purpose = purpose;
		proposal.dateTime = dateTime;
		proposal.createdAt = Instant.now();
		return proposal;
	}

	ReservationProposalResponse toResponse() {
		return new ReservationProposalResponse(
				id,
				status,
				customerName,
				branch,
				purpose,
				dateTime
		);
	}
}
