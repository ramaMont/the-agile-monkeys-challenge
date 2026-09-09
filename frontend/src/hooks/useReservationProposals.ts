import { useQuery } from '@tanstack/react-query'
import { fetchReservationProposals } from '../api/reservationProposals'

export const reservationProposalsQueryKey = ['reservationProposals'] as const

export function useReservationProposals() {
  return useQuery({
    queryKey: reservationProposalsQueryKey,
    queryFn: fetchReservationProposals,
  })
}
