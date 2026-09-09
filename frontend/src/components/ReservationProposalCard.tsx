import type { ReservationProposal } from '../types/reservationProposal'
import { ProposalDecisionButtons } from './ProposalDecisionButtons'
import { ProposalStatusBadge } from './ProposalStatusBadge'

type Props = {
  proposal: ReservationProposal
}

export function ReservationProposalCard({ proposal }: Props) {
  const shortId = proposal.id.slice(0, 8)
  const isPending = proposal.status === 'PENDING'

  return (
    <article className="rounded-xl border border-zinc-200 bg-white p-5 shadow-sm dark:border-zinc-800 dark:bg-zinc-900">
      <header className="flex items-start justify-between gap-3">
        <h2 className="text-lg font-medium text-zinc-900 dark:text-zinc-50">
          {proposal.customerName}
        </h2>
        <ProposalStatusBadge status={proposal.status} />
      </header>

      <dl className="mt-4 space-y-2 text-sm">
        <div>
          <dt className="text-zinc-500 dark:text-zinc-400">Purpose</dt>
          <dd className="text-zinc-800 dark:text-zinc-200">{proposal.purpose}</dd>
        </div>
        <div>
          <dt className="text-zinc-500 dark:text-zinc-400">Branch</dt>
          <dd className="text-zinc-800 dark:text-zinc-200">{proposal.branch}</dd>
        </div>
        <div>
          <dt className="text-zinc-500 dark:text-zinc-400">Date & time</dt>
          <dd className="text-zinc-800 dark:text-zinc-200">{proposal.dateTime}</dd>
        </div>
      </dl>

      {isPending ? <ProposalDecisionButtons proposalId={proposal.id} /> : null}

      <footer className="mt-4 border-t border-zinc-100 pt-3 text-xs text-zinc-400 dark:border-zinc-800 dark:text-zinc-500">
        {shortId}…
      </footer>
    </article>
  )
}
