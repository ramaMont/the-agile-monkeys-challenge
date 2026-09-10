import { env } from '../config/env'
import type {
  ReservationProposal,
  ReservationProposalPage,
} from '../types/reservationProposal'
import { apiFetch } from './client'

export type FetchReservationProposalsParams = {
  page: number
  size: number
}

export async function fetchReservationProposals(
  params: FetchReservationProposalsParams,
): Promise<ReservationProposalPage> {
  const query = new URLSearchParams({
    page: String(params.page),
    size: String(params.size),
  })
  const response = await apiFetch(
    `${env.apiBaseUrl}/v1/reservation-proposals?${query}`,
  )
  if (!response.ok) {
    throw new Error(`Reservation proposals request failed: ${response.status}`)
  }
  return response.json()
}

export type ReservationProposalStatusUpdate = {
  status: 'APPROVED' | 'REJECTED'
}

export class ProposalUpdateError extends Error {
  readonly status: number

  constructor(status: number) {
    super(
      status === 409
        ? 'This proposal was already decided.'
        : `Reservation proposal update failed: ${status}`,
    )
    this.name = 'ProposalUpdateError'
    this.status = status
  }
}

export async function updateReservationProposal(
  id: string,
  update: ReservationProposalStatusUpdate,
): Promise<ReservationProposal> {
  const response = await apiFetch(`${env.apiBaseUrl}/v1/reservation-proposals/${id}`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(update),
  })
  if (!response.ok) {
    throw new ProposalUpdateError(response.status)
  }
  return response.json()
}
