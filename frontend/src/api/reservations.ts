import { env } from '../config/env'

export async function fetchReservations(): Promise<unknown[]> {
  const response = await fetch(`${env.apiBaseUrl}/v1/actions/reservations`)
  if (!response.ok) {
    throw new Error(`Reservations request failed: ${response.status}`)
  }
  return response.json()
}
