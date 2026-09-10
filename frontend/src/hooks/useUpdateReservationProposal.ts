import { useMutation, useQueryClient } from '@tanstack/react-query'
import {
  ProposalUpdateError,
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
    onError: async (error) => {
      if (error instanceof ProposalUpdateError && error.status === 409) {
        await queryClient.invalidateQueries({ queryKey: reservationProposalsQueryKey })
      }
    },
  })
}
