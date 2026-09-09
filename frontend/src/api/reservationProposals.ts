import { env } from '../config/env'

export async function fetchReservationProposals(): Promise<unknown[]> {
  const response = await fetch(`${env.apiBaseUrl}/v1/reservation-proposals`)
  if (!response.ok) {
    throw new Error(`Reservation proposals request failed: ${response.status}`)
  }
  return response.json()
}
