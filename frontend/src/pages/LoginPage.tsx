import { Navigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthProvider'
import { LoginForm } from '../components/LoginForm'

export function LoginPage() {
  const { token } = useAuth()

  if (token) {
    return <Navigate to="/" replace />
  }

  return (
    <main className="mx-auto max-w-md px-6 py-10">
      <h1 className="text-3xl font-medium tracking-tight text-zinc-900 dark:text-zinc-50">
        Proposal review
      </h1>
      <p className="mt-2 mb-6 text-sm text-zinc-500 dark:text-zinc-400">
        Operator console for confirming actions proposed by the Bank Assistant agent.
      </p>
      <LoginForm />
    </main>
  )
}
