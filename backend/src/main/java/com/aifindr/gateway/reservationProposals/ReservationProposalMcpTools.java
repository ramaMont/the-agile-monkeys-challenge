package com.aifindr.gateway.reservationProposals;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class ReservationProposalMcpTools {

	private final ReservationProposalService proposals;

	public ReservationProposalMcpTools(ReservationProposalService proposals) {
		this.proposals = proposals;
	}

	@McpTool(
			name = "propose_reservation",
			description = "Propose a simulated bank branch appointment (turno). Creates a PENDING proposal; does not approve or reject it."
	)
	public ReservationProposalResponse proposeReservation(
			@McpToolParam(description = "Customer full name", required = true) String customerName,
			@McpToolParam(description = "Bank branch name or code", required = true) String branch,
			@McpToolParam(description = "Reason for the visit, e.g. mortgage advice or account opening", required = true)
			String purpose,
			@McpToolParam(description = "Appointment date and time in ISO-8601, e.g. 2026-09-20T10:00", required = true)
			String dateTime) {
		return proposals.propose(customerName, branch, purpose, dateTime);
	}
}
