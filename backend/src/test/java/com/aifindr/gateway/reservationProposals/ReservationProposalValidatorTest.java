package com.aifindr.gateway.reservationProposals;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReservationProposalValidatorTest {

	@Test
	void requireProposalAcceptsLocalAndOffsetDateTime() {
		ReservationProposalValidator.NormalizedProposal local = ReservationProposalValidator.requireProposal(
				" Ada Lovelace ",
				"Madrid Centro",
				"Mortgage advice",
				"2026-09-20T10:00"
		);
		assertEquals("Ada Lovelace", local.customerName());
		assertEquals("2026-09-20T10:00", local.dateTime());

		ReservationProposalValidator.NormalizedProposal offset = ReservationProposalValidator.requireProposal(
				"Ada Lovelace",
				"Madrid Centro",
				"Mortgage advice",
				"2026-09-20T10:00:00Z"
		);
		assertEquals("2026-09-20T10:00:00Z", offset.dateTime());
	}

	@Test
	void requireProposalRejectsBlank() {
		IllegalArgumentException ex = assertThrows(
				IllegalArgumentException.class,
				() -> ReservationProposalValidator.requireProposal(
						"",
						"Madrid Centro",
						"Mortgage advice",
						"2026-09-20T10:00"
				)
		);
		assertTrue(ex.getMessage().contains("customerName"));
	}

	@Test
	void requireProposalRejectsNonIsoDateTime() {
		IllegalArgumentException ex = assertThrows(
				IllegalArgumentException.class,
				() -> ReservationProposalValidator.requireProposal(
						"Ada Lovelace",
						"Madrid Centro",
						"Mortgage advice",
						"not-a-date"
				)
		);
		assertTrue(ex.getMessage().contains("ISO-8601"));
	}

	@Test
	void requireProposalRejectsOversizedField() {
		IllegalArgumentException ex = assertThrows(
				IllegalArgumentException.class,
				() -> ReservationProposalValidator.requireProposal(
						"a".repeat(ReservationProposalValidator.MAX_CUSTOMER_NAME + 1),
						"Madrid Centro",
						"Mortgage advice",
						"2026-09-20T10:00"
				)
		);
		assertTrue(ex.getMessage().contains("customerName"));
	}

	@Test
	void requirePageRejectsNegativePageAndHugeSize() {
		assertThrows(IllegalArgumentException.class, () -> ReservationProposalValidator.requirePage(-1, 10));
		assertThrows(IllegalArgumentException.class, () -> ReservationProposalValidator.requirePage(0, 0));
		assertThrows(
				IllegalArgumentException.class,
				() -> ReservationProposalValidator.requirePage(0, ReservationProposalValidator.MAX_PAGE_SIZE + 1)
		);
		ReservationProposalValidator.requirePage(0, ReservationProposalValidator.MAX_PAGE_SIZE);
	}
}
