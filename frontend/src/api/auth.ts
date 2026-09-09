import { env } from '../config/env'

export type LoginPayload = {
  username: string
  password: string
}

export type LoginResult = {
  token: string
}

export async function login(payload: LoginPayload): Promise<LoginResult> {
  const response = await fetch(`${env.apiBaseUrl}/v1/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })
  if (!response.ok) {
    throw new Error(`Login failed: ${response.status}`)
  }
  return response.json()
}
