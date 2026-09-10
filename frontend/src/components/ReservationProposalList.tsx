import { useState } from 'react'
import { useReservationProposals } from '../hooks/useReservationProposals'
import { ReservationProposalCard } from './ReservationProposalCard'

export function ReservationProposalList() {
  const [page, setPage] = useState(0)
  const { data, isPending, isError, refetch, isFetching } = useReservationProposals(page)

  if (isError && !data) {
    return (
      <div
        role="alert"
        className="rounded-xl border border-amber-200 bg-amber-50 px-4 py-3 text-amber-800 dark:border-amber-900 dark:bg-amber-950/40 dark:text-amber-300"
      >
        <p>Could not load reservation proposals from the backend.</p>
        <button
          type="button"
          onClick={() => void refetch()}
          disabled={isFetching}
          className="mt-3 rounded-lg border border-amber-300 px-3 py-1.5 text-sm font-medium disabled:opacity-50 dark:border-amber-800"
        >
          {isFetching ? 'Retrying…' : 'Retry'}
        </button>
      </div>
    )
  }

  if (isPending) {
    return (
      <div className="rounded-xl border border-zinc-200 px-4 py-8 text-center text-zinc-500 dark:border-zinc-800 dark:text-zinc-400">
        Loading reservation proposals…
      </div>
    )
  }

  if (!data?.content) {
    return (
      <div
        role="alert"
        className="rounded-xl border border-amber-200 bg-amber-50 px-4 py-3 text-amber-800 dark:border-amber-900 dark:bg-amber-950/40 dark:text-amber-300"
      >
        Could not read proposals from the server. Try again in a moment.
      </div>
    )
  }

  const totalPages = Math.max(data.totalPages, 1)
  const items = data.content

  return (
    <div className="grid gap-4">
      {items.length === 0 ? (
        <div className="rounded-xl border border-dashed border-zinc-300 px-4 py-8 text-center text-zinc-500 dark:border-zinc-700 dark:text-zinc-400">
          No reservation proposals yet.
        </div>
      ) : (
        <ul className="grid gap-4">
          {items.map((item) => (
            <li key={item.id}>
              <ReservationProposalCard proposal={item} />
            </li>
          ))}
        </ul>
      )}

      <div className="flex items-center justify-between gap-3">
        <button
          type="button"
          disabled={page === 0}
          onClick={() => setPage((current) => Math.max(0, current - 1))}
          className="rounded-lg border border-zinc-300 px-3 py-1.5 text-sm text-zinc-700 disabled:opacity-50 dark:border-zinc-700 dark:text-zinc-200"
        >
          Previous
        </button>
        <p className="text-sm text-zinc-500 dark:text-zinc-400">
          Page {page + 1} of {totalPages}
        </p>
        <button
          type="button"
          disabled={page >= data.totalPages - 1 || data.totalPages === 0}
          onClick={() => setPage((current) => current + 1)}
          className="rounded-lg border border-zinc-300 px-3 py-1.5 text-sm text-zinc-700 disabled:opacity-50 dark:border-zinc-700 dark:text-zinc-200"
        >
          Next
        </button>
      </div>
    </div>
  )
}
