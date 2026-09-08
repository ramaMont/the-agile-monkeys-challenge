import { useQuery } from '@tanstack/react-query'
import { fetchReservations } from '../api/reservations'

export const reservationsQueryKey = ['reservations'] as const

export function useReservations() {
  return useQuery({
    queryKey: reservationsQueryKey,
    queryFn: fetchReservations,
  })
}
