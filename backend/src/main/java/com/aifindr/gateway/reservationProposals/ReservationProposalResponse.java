package com.aifindr.gateway.reservationProposals;

public record ReservationProposalResponse(
		String id,
		ReservationProposalStatus status,
		String customerName,
		String branch,
		String purpose,
		String dateTime
) {
}
