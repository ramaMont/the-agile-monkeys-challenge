import { env } from '../config/env'
import type { ReservationProposal } from '../types/reservationProposal'

export async function fetchReservationProposals(): Promise<ReservationProposal[]> {
  const response = await fetch(`${env.apiBaseUrl}/v1/reservation-proposals`)
  if (!response.ok) {
    throw new Error(`Reservation proposals request failed: ${response.status}`)
  }
  return response.json()
}

export type ReservationProposalStatusUpdate = {
  status: 'APPROVED' | 'REJECTED'
}

export async function updateReservationProposal(
  id: string,
  update: ReservationProposalStatusUpdate,
): Promise<ReservationProposal> {
  const response = await fetch(`${env.apiBaseUrl}/v1/reservation-proposals/${id}`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(update),
  })
  if (!response.ok) {
    throw new Error(`Reservation proposal update failed: ${response.status}`)
  }
  return response.json()
}
