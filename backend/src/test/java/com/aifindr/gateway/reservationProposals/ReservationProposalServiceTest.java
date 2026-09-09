package com.aifindr.gateway.reservationProposals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
		assertEquals(1, proposals.list().size());
		assertEquals(created, proposals.list().get(0));
	}
}
