import { useMutation, useQueryClient } from '@tanstack/react-query'
import {
  updateReservationProposal,
  type ReservationProposalStatusUpdate,
} from '../api/reservationProposals'
import { reservationProposalsQueryKey } from './useReservationProposals'

type UpdateVars = {
  id: string
  status: ReservationProposalStatusUpdate['status']
}

export function useUpdateReservationProposal() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, status }: UpdateVars) =>
      updateReservationProposal(id, { status }),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: reservationProposalsQueryKey })
    },
  })
}
