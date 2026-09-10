package com.aifindr.gateway.reservationProposals;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

final class ReservationProposalValidator {

	static final int MAX_CUSTOMER_NAME = 200;
	static final int MAX_BRANCH = 120;
	static final int MAX_PURPOSE = 500;
	static final int MAX_DATE_TIME = 40;
	static final int MAX_PAGE_SIZE = 50;

	private ReservationProposalValidator() {
	}

	static NormalizedProposal requireProposal(
			String customerName,
			String branch,
			String purpose,
			String dateTime) {
		return new NormalizedProposal(
				requireText("customerName", customerName, MAX_CUSTOMER_NAME),
				requireText("branch", branch, MAX_BRANCH),
				requireText("purpose", purpose, MAX_PURPOSE),
				requireIsoDateTime(dateTime)
		);
	}

	static void requirePage(int page, int size) {
		if (page < 0) {
			throw new IllegalArgumentException("page must be >= 0");
		}
		if (size < 1 || size > MAX_PAGE_SIZE) {
			throw new IllegalArgumentException("size must be between 1 and " + MAX_PAGE_SIZE);
		}
	}

	record NormalizedProposal(String customerName, String branch, String purpose, String dateTime) {
	}

	private static String requireIsoDateTime(String dateTime) {
		String value = requireText("dateTime", dateTime, MAX_DATE_TIME);
		if (isLocalDateTime(value) || isOffsetDateTime(value)) {
			return value;
		}
		throw new IllegalArgumentException("dateTime must be ISO-8601");
	}

	private static boolean isLocalDateTime(String value) {
		try {
			LocalDateTime.parse(value);
			return true;
		}
		catch (DateTimeParseException ex) {
			return false;
		}
	}

	private static boolean isOffsetDateTime(String value) {
		try {
			OffsetDateTime.parse(value);
			return true;
		}
		catch (DateTimeParseException ex) {
			return false;
		}
	}

	private static String requireText(String field, String value, int maxLength) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException(field + " must not be blank");
		}
		String trimmed = value.strip();
		if (trimmed.length() > maxLength) {
			throw new IllegalArgumentException(field + " must be at most " + maxLength + " characters");
		}
		return trimmed;
	}
}
