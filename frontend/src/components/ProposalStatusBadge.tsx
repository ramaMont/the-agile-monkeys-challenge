import type { ReservationProposalStatus } from '../types/reservationProposal'

const statusStyles: Record<ReservationProposalStatus, string> = {
  PENDING:
    'bg-amber-100 text-amber-800 dark:bg-amber-950 dark:text-amber-300',
  APPROVED:
    'bg-emerald-100 text-emerald-800 dark:bg-emerald-950 dark:text-emerald-300',
  REJECTED: 'bg-red-100 text-red-800 dark:bg-red-950 dark:text-red-300',
}

type Props = {
  status: ReservationProposalStatus
}

export function ProposalStatusBadge({ status }: Props) {
  return (
    <span
      className={`inline-flex rounded-full px-2.5 py-0.5 text-xs font-medium tracking-wide ${statusStyles[status]}`}
    >
      {status}
    </span>
  )
}
