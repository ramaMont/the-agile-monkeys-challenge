import { useReservationProposals } from '../hooks/useReservationProposals'
import { ReservationProposalCard } from './ReservationProposalCard'

export function ReservationProposalList() {
  const { data: items, isPending, isError } = useReservationProposals()

  if (isError) {
    return (
      <div
        role="alert"
        className="rounded-xl border border-amber-200 bg-amber-50 px-4 py-3 text-amber-800 dark:border-amber-900 dark:bg-amber-950/40 dark:text-amber-300"
      >
        Could not load reservation proposals from the backend.
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

  if (items.length === 0) {
    return (
      <div className="rounded-xl border border-dashed border-zinc-300 px-4 py-8 text-center text-zinc-500 dark:border-zinc-700 dark:text-zinc-400">
        No reservation proposals yet.
      </div>
    )
  }

  return (
    <ul className="grid gap-4">
      {items.map((item) => (
        <li key={item.id}>
          <ReservationProposalCard proposal={item} />
        </li>
      ))}
    </ul>
  )
}
