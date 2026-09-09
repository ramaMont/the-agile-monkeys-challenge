import { useReservationProposals } from '../hooks/useReservationProposals'

export function ReservationProposalList() {
  const { data: items, isPending, isError } = useReservationProposals()

  if (isError) {
    return (
      <p role="alert" className="text-amber-700 dark:text-amber-400">
        Could not load reservation proposals from the backend.
      </p>
    )
  }

  if (isPending) {
    return <p className="text-zinc-500 dark:text-zinc-400">Loading reservation proposals…</p>
  }

  if (items.length === 0) {
    return <p className="text-zinc-500 dark:text-zinc-400">No reservation proposals yet.</p>
  }

  return (
    <ul className="divide-y divide-zinc-200 dark:divide-zinc-800">
      {items.map((item, index) => (
        <li key={index} className="py-3 text-zinc-800 dark:text-zinc-200">
          {JSON.stringify(item)}
        </li>
      ))}
    </ul>
  )
}
