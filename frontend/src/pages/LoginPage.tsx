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
      <h1 className="mb-6 text-3xl font-medium tracking-tight text-zinc-900 dark:text-zinc-50">
        Reservation proposals
      </h1>
      <LoginForm />
    </main>
  )
}
