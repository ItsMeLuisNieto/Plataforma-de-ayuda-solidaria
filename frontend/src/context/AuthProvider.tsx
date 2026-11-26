import { useCallback, useEffect, useMemo, useState } from 'react'
import type { ReactNode } from 'react'
import { apiFetch, invalidarCsrf } from '../lib/api'
import type { UsuarioActual } from '../types'
import { AuthContext } from './authContext'

export function AuthProvider({ children }: { children: ReactNode }) {
  const [usuario, setUsuario] = useState<UsuarioActual | null>(null)
  const [cargando, setCargando] = useState(true)

  const recargar = useCallback(async () => {
    try {
      const respuesta = await fetch('/api/usuario/actual', { credentials: 'include' })
      setUsuario(respuesta.ok ? ((await respuesta.json()) as UsuarioActual) : null)
    } catch {
      setUsuario(null)
    } finally {
      setCargando(false)
    }
  }, [])

  useEffect(() => {
    queueMicrotask(() => {
      void recargar()
    })
  }, [recargar])

  const cerrarSesion = useCallback(async () => {
    await apiFetch('/logout', { method: 'POST' })
    invalidarCsrf()
    setUsuario(null)
  }, [])

  const valor = useMemo(
    () => ({ usuario, cargando, recargar, cerrarSesion }),
    [usuario, cargando, recargar, cerrarSesion],
  )

  return <AuthContext.Provider value={valor}>{children}</AuthContext.Provider>
}
