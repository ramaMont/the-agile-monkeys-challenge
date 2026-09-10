package com.aifindr.gateway.reservationProposals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@SpringBootTest
@Transactional
class ReservationProposalServiceValidationTest {

	@Autowired
	private ReservationProposalService proposals;

	@Test
	void proposeRejectsBlankCustomerName() {
		ResponseStatusException ex = assertThrows(
				ResponseStatusException.class,
				() -> proposals.propose("", "Madrid Centro", "Mortgage advice", "2026-09-20T10:00")
		);
		assertEquals(BAD_REQUEST, ex.getStatusCode());
	}

	@Test
	void proposeRejectsNonIsoDateTime() {
		ResponseStatusException ex = assertThrows(
				ResponseStatusException.class,
				() -> proposals.propose("Ada Lovelace", "Madrid Centro", "Mortgage advice", "not-a-date")
		);
		assertEquals(BAD_REQUEST, ex.getStatusCode());
	}

	@Test
	void proposeRejectsOversizedCustomerName() {
		ResponseStatusException ex = assertThrows(
				ResponseStatusException.class,
				() -> proposals.propose(
						"a".repeat(ReservationProposalValidator.MAX_CUSTOMER_NAME + 1),
						"Madrid Centro",
						"Mortgage advice",
						"2026-09-20T10:00"
				)
		);
		assertEquals(BAD_REQUEST, ex.getStatusCode());
	}

	@Test
	void listRejectsOutOfRangePageSize() {
		ResponseStatusException negativePage = assertThrows(
				ResponseStatusException.class,
				() -> proposals.list(-1, 10)
		);
		assertEquals(BAD_REQUEST, negativePage.getStatusCode());

		ResponseStatusException hugeSize = assertThrows(
				ResponseStatusException.class,
				() -> proposals.list(0, ReservationProposalValidator.MAX_PAGE_SIZE + 1)
		);
		assertEquals(BAD_REQUEST, hugeSize.getStatusCode());
	}
}
