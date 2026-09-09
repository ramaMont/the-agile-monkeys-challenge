import { ReservationProposalList } from './components/ReservationProposalList'

function App() {
  return (
    <main className="mx-auto max-w-2xl px-6 py-10">
      <h1 className="text-3xl font-medium tracking-tight text-zinc-900 dark:text-zinc-50">
        Reservation proposals
      </h1>
      <p className="mt-2 mb-6 text-zinc-500 dark:text-zinc-400">
        Actions waiting for review from the MCP gateway.
      </p>
      <ReservationProposalList />
    </main>
  )
}

export default App
