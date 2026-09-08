import { ReservationList } from './components/ReservationList'

function App() {
  return (
    <main className="mx-auto max-w-xl px-6 py-10">
      <h1 className="text-3xl font-medium tracking-tight text-zinc-900 dark:text-zinc-50">
        Reservations
      </h1>
      <p className="mt-2 mb-6 text-zinc-500 dark:text-zinc-400">
        Actions waiting for review from the MCP gateway.
      </p>
      <ReservationList />
    </main>
  )
}

export default App
