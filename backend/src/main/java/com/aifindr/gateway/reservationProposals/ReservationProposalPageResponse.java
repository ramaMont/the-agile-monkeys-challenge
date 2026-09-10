package com.aifindr.gateway.reservationProposals;

import java.util.List;

public record ReservationProposalPageResponse(
		List<ReservationProposalResponse> content,
		int page,
		int size,
		long totalElements,
		int totalPages) {
}
