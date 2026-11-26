import type { ReactNode } from 'react'
import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/authContext'

export function RutaAdmin({ children }: { children: ReactNode }) {
  const { usuario, cargando } = useAuth()

  if (cargando) {
    return (
      <div className="d-flex justify-content-center py-5">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Verificando permisos...</span>
        </div>
      </div>
    )
  }

  if (usuario?.rol !== 'ADMIN') {
    return <Navigate to="/" replace />
  }

  return <>{children}</>
}
