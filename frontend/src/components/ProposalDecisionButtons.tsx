import { useUpdateReservationProposal } from '../hooks/useUpdateReservationProposal'

type Props = {
  proposalId: string
}

export function ProposalDecisionButtons({ proposalId }: Props) {
  const { mutate, isPending, isError } = useUpdateReservationProposal()

  return (
    <div className="mt-4 flex flex-wrap items-center gap-2 border-t border-zinc-100 pt-4 dark:border-zinc-800">
      <button
        type="button"
        disabled={isPending}
        onClick={() => mutate({ id: proposalId, status: 'APPROVED' })}
        className="rounded-lg bg-emerald-600 px-3 py-1.5 text-sm font-medium text-white disabled:opacity-50 dark:bg-emerald-500"
      >
        Accept
      </button>
      <button
        type="button"
        disabled={isPending}
        onClick={() => mutate({ id: proposalId, status: 'REJECTED' })}
        className="rounded-lg bg-red-600 px-3 py-1.5 text-sm font-medium text-white disabled:opacity-50 dark:bg-red-500"
      >
        Reject
      </button>
      {isError ? (
        <p role="alert" className="w-full text-sm text-red-600 dark:text-red-400">
          Could not update proposal.
        </p>
      ) : null}
    </div>
  )
}
