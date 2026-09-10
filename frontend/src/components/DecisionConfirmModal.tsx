import { useEffect, useId, useRef } from 'react'
import { createPortal } from 'react-dom'
import type { ReservationProposalStatus } from '../types/reservationProposal'

type Decision = Extract<ReservationProposalStatus, 'APPROVED' | 'REJECTED'>

type Props = {
  decision: Decision
  isPending: boolean
  errorMessage: string | null
  onConfirm: () => void
  onCancel: () => void
}

export function DecisionConfirmModal({
  decision,
  isPending,
  errorMessage,
  onConfirm,
  onCancel,
}: Props) {
  const titleId = useId()
  const confirmRef = useRef<HTMLButtonElement>(null)
  const actionLabel = decision === 'APPROVED' ? 'Approve' : 'Reject'
  const label = decision === 'APPROVED' ? 'approve' : 'reject'
  const confirmClass =
    decision === 'APPROVED'
      ? 'rounded-lg bg-emerald-600 px-3 py-1.5 text-sm font-medium text-white disabled:opacity-50 dark:bg-emerald-500'
      : 'rounded-lg bg-red-600 px-3 py-1.5 text-sm font-medium text-white disabled:opacity-50 dark:bg-red-500'

  useEffect(() => {
    confirmRef.current?.focus()
    function onKeyDown(event: KeyboardEvent) {
      if (event.key === 'Escape' && !isPending) onCancel()
    }
    window.addEventListener('keydown', onKeyDown)
    return () => window.removeEventListener('keydown', onKeyDown)
  }, [isPending, onCancel])

  return createPortal(
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-zinc-950/50 p-4"
      onClick={() => {
        if (!isPending) onCancel()
      }}
    >
      <div
        role="dialog"
        aria-modal="true"
        aria-labelledby={titleId}
        className="w-full max-w-sm rounded-xl border border-zinc-200 bg-white p-5 shadow-lg dark:border-zinc-700 dark:bg-zinc-900"
        onClick={(event) => event.stopPropagation()}
      >
        <h3
          id={titleId}
          className="text-lg font-medium text-zinc-900 dark:text-zinc-50"
        >
          Confirm decision
        </h3>
        <p className="mt-2 text-sm text-zinc-600 dark:text-zinc-300">
          Confirm you want to {label} this proposal? This cannot be undone.
        </p>
        {errorMessage ? (
          <p role="alert" className="mt-3 text-sm text-red-600 dark:text-red-400">
            {errorMessage}
          </p>
        ) : null}
        <div className="mt-5 flex flex-wrap justify-end gap-2">
          <button
            type="button"
            disabled={isPending}
            onClick={onCancel}
            className="rounded-lg border border-zinc-300 px-3 py-1.5 text-sm text-zinc-700 disabled:opacity-50 dark:border-zinc-700 dark:text-zinc-200"
          >
            Cancel
          </button>
          <button
            ref={confirmRef}
            type="button"
            disabled={isPending}
            onClick={onConfirm}
            className={confirmClass}
          >
            {isPending ? `${actionLabel}…` : actionLabel}
          </button>
        </div>
      </div>
    </div>,
    document.body,
  )
}
