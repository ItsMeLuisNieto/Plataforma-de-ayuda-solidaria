import { Link } from 'react-router-dom'

export default function NotFound() {
  return (
    <main className="container py-5 text-center">
      <h1 className="display-5 fw-bold text-dark">Página no encontrada</h1>
      <p className="text-secondary">La ruta que buscas no existe en Kallpa Solidary.</p>
      <Link to="/" className="btn btn-primary mt-3">
        Volver al inicio
      </Link>
    </main>
  )
}
