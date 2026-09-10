import { useCallback, useState } from 'react'
import { ProposalUpdateError } from '../api/reservationProposals'
import { useUpdateReservationProposal } from '../hooks/useUpdateReservationProposal'
import type { ReservationProposalStatus } from '../types/reservationProposal'
import { DecisionConfirmModal } from './DecisionConfirmModal'

type Decision = Extract<ReservationProposalStatus, 'APPROVED' | 'REJECTED'>

type Props = {
  proposalId: string
}

export function ProposalDecisionButtons({ proposalId }: Props) {
  const { mutate, isPending, isError, error, reset } = useUpdateReservationProposal()
  const [pendingDecision, setPendingDecision] = useState<Decision | null>(null)

  const cancel = useCallback(() => {
    reset()
    setPendingDecision(null)
  }, [reset])

  function choose(decision: Decision) {
    reset()
    setPendingDecision(decision)
  }

  function confirm() {
    if (!pendingDecision) return
    mutate(
      { id: proposalId, status: pendingDecision },
      { onSuccess: () => setPendingDecision(null) },
    )
  }

  const errorMessage =
    isError && pendingDecision
      ? error instanceof ProposalUpdateError
        ? error.message
        : 'Could not update proposal.'
      : null

  return (
    <>
      <div className="mt-4 flex flex-wrap items-center gap-2 border-t border-zinc-100 pt-4 dark:border-zinc-800">
        <button
          type="button"
          disabled={isPending}
          onClick={() => choose('APPROVED')}
          className="rounded-lg bg-emerald-600 px-3 py-1.5 text-sm font-medium text-white disabled:opacity-50 dark:bg-emerald-500"
        >
          Approve
        </button>
        <button
          type="button"
          disabled={isPending}
          onClick={() => choose('REJECTED')}
          className="rounded-lg bg-red-600 px-3 py-1.5 text-sm font-medium text-white disabled:opacity-50 dark:bg-red-500"
        >
          Reject
        </button>
        {isError && !pendingDecision ? (
          <p role="alert" className="w-full text-sm text-red-600 dark:text-red-400">
            {error instanceof ProposalUpdateError
              ? error.message
              : 'Could not update proposal.'}
          </p>
        ) : null}
      </div>

      {pendingDecision ? (
        <DecisionConfirmModal
          decision={pendingDecision}
          isPending={isPending}
          errorMessage={errorMessage}
          onConfirm={confirm}
          onCancel={cancel}
        />
      ) : null}
    </>
  )
}
