function requiredEnv(name: keyof ImportMetaEnv): string {
  const value = import.meta.env[name]
  if (typeof value !== 'string' || value.trim() === '') {
    throw new Error(`Missing ${name}. Set it in frontend/.env`)
  }
  return value.replace(/\/$/, '')
}

export const env = {
  apiBaseUrl: requiredEnv('VITE_API_BASE_URL'),
}
