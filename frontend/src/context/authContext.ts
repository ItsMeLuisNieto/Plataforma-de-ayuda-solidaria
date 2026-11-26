import { createContext, useContext } from 'react'
import type { UsuarioActual } from '../types'

export interface AuthContextValue {
  usuario: UsuarioActual | null
  cargando: boolean
  recargar: () => Promise<void>
  cerrarSesion: () => Promise<void>
}

export const AuthContext = createContext<AuthContextValue | null>(null)

export function useAuth(): AuthContextValue {
  const contexto = useContext(AuthContext)
  if (!contexto) {
    throw new Error('useAuth debe usarse dentro de AuthProvider')
  }
  return contexto
}
