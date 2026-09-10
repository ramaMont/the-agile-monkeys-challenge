package com.aifindr.gateway.reservationProposals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@SpringBootTest
@Transactional
class ReservationProposalServiceTest {

	@Autowired
	private ReservationProposalService proposals;

	@Test
	void proposeStoresProposalAsPending() {
		ReservationProposalResponse created = proposals.propose(
				"Ada Lovelace",
				"Madrid Centro",
				"Mortgage advice",
				"2026-09-20T10:00"
		);

		assertEquals(ReservationProposalStatus.PENDING, created.status());
		assertEquals("Ada Lovelace", created.customerName());
		assertEquals("Madrid Centro", created.branch());
		assertEquals("Mortgage advice", created.purpose());
		assertEquals("2026-09-20T10:00", created.dateTime());
		assertFalse(created.id().isBlank());
		ReservationProposalPageResponse page = proposals.list(0, 10);
		assertEquals(1, page.content().size());
		assertEquals(created, page.content().get(0));
		assertEquals(1, page.totalElements());
	}

	@Test
	void updateStatusApprovesPendingProposal() {
		ReservationProposalResponse created = proposals.propose(
				"Ada Lovelace",
				"Madrid Centro",
				"Mortgage advice",
				"2026-09-20T10:00"
		);

		ReservationProposalResponse updated = proposals.updateStatus(
				created.id(),
				ReservationProposalStatus.APPROVED
		);

		assertEquals(ReservationProposalStatus.APPROVED, updated.status());
		assertEquals(created.id(), updated.id());
	}

	@Test
	void updateStatusRejectsPendingProposal() {
		ReservationProposalResponse created = proposals.propose(
				"Ada Lovelace",
				"Madrid Centro",
				"Mortgage advice",
				"2026-09-20T10:00"
		);

		ReservationProposalResponse updated = proposals.updateStatus(
				created.id(),
				ReservationProposalStatus.REJECTED
		);

		assertEquals(ReservationProposalStatus.REJECTED, updated.status());
	}

	@Test
	void updateStatusConflictsWhenNotPending() {
		ReservationProposalResponse created = proposals.propose(
				"Ada Lovelace",
				"Madrid Centro",
				"Mortgage advice",
				"2026-09-20T10:00"
		);
		proposals.updateStatus(created.id(), ReservationProposalStatus.APPROVED);

		ResponseStatusException ex = assertThrows(
				ResponseStatusException.class,
				() -> proposals.updateStatus(created.id(), ReservationProposalStatus.REJECTED)
		);
		assertEquals(CONFLICT, ex.getStatusCode());
	}

	@Test
	void updateStatusNotFoundForUnknownId() {
		ResponseStatusException ex = assertThrows(
				ResponseStatusException.class,
				() -> proposals.updateStatus("missing", ReservationProposalStatus.APPROVED)
		);
		assertEquals(NOT_FOUND, ex.getStatusCode());
	}

	@Test
	void updateStatusRejectsPendingTarget() {
		ReservationProposalResponse created = proposals.propose(
				"Ada Lovelace",
				"Madrid Centro",
				"Mortgage advice",
				"2026-09-20T10:00"
		);

		ResponseStatusException ex = assertThrows(
				ResponseStatusException.class,
				() -> proposals.updateStatus(created.id(), ReservationProposalStatus.PENDING)
		);
		assertEquals(BAD_REQUEST, ex.getStatusCode());
	}

	@Test
	void getReturnsPendingProposal() {
		ReservationProposalResponse created = proposals.propose(
				"Ada Lovelace",
				"Madrid Centro",
				"Mortgage advice",
				"2026-09-20T10:00"
		);

		ReservationProposalResponse found = proposals.get(created.id());

		assertEquals(created.id(), found.id());
		assertEquals(ReservationProposalStatus.PENDING, found.status());
		assertEquals(created, found);
	}

	@Test
	void getNotFoundForUnknownId() {
		ResponseStatusException ex = assertThrows(
				ResponseStatusException.class,
				() -> proposals.get("missing")
		);
		assertEquals(NOT_FOUND, ex.getStatusCode());
	}
}
