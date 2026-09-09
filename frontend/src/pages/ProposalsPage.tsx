import { useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthProvider'
import { ReservationProposalList } from '../components/ReservationProposalList'

export function ProposalsPage() {
  const { logout } = useAuth()
  const navigate = useNavigate()

  function onLogout() {
    logout()
    navigate('/login', { replace: true })
  }

  return (
    <main className="mx-auto max-w-2xl px-6 py-10">
      <div className="mb-6 flex items-start justify-between gap-4">
        <div>
          <h1 className="text-3xl font-medium tracking-tight text-zinc-900 dark:text-zinc-50">
            Reservation proposals
          </h1>
          <p className="mt-2 text-zinc-500 dark:text-zinc-400">
            Actions waiting for review from the MCP gateway.
          </p>
        </div>
        <button
          type="button"
          onClick={onLogout}
          className="rounded-lg border border-zinc-300 px-3 py-1.5 text-sm text-zinc-700 hover:bg-zinc-50 dark:border-zinc-700 dark:text-zinc-200 dark:hover:bg-zinc-800"
        >
          Log out
        </button>
      </div>
      <ReservationProposalList />
    </main>
  )
}
