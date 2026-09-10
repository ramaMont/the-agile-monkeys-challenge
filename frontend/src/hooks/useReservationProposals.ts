import { useQuery } from '@tanstack/react-query'
import { fetchReservationProposals } from '../api/reservationProposals'

export const PAGE_SIZE = 10

export const reservationProposalsQueryKey = ['reservationProposals'] as const

export function useReservationProposals(page: number) {
  return useQuery({
    queryKey: [...reservationProposalsQueryKey, page, PAGE_SIZE],
    queryFn: () => fetchReservationProposals({ page, size: PAGE_SIZE }),
    refetchInterval: 15_000,
  })
}
