package com.aifindr.gateway.reservations;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/actions/reservations")
public class ReservationController {

	private final ReservationService reservations;

	public ReservationController(ReservationService reservations) {
		this.reservations = reservations;
	}

	@GetMapping
	public List<ReservationResponse> list() {
		return reservations.list();
	}
}
