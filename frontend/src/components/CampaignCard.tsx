import { formatearEntero, tituloCampana } from '../lib/format'
import type { Campana } from '../types'

interface CampaignCardProps {
  campana: Campana
  esAdmin: boolean
  onDonar: (campana: Campana) => void
  onEliminar: (campana: Campana) => void
}

export function CampaignCard({ campana, esAdmin, onDonar, onEliminar }: CampaignCardProps) {
  const colorBarra =
    campana.porcentaje >= 80 ? 'bg-success' : campana.porcentaje > 40 ? 'bg-warning' : 'bg-danger'

  return (
    <div className="col-lg-4">
      <div className="campaign-card card h-100 border-0 shadow-sm rounded-4 p-4">
        <div className="d-flex justify-content-between align-items-center mb-3">
          <span className="badge bg-warning-subtle text-warning-emphasis fw-semibold px-3 py-2">
            En curso
          </span>
          <span className="small text-secondary">{campana.metaDias} días</span>
        </div>

        <h3 className="fw-bold text-dark mb-2">{tituloCampana(campana)}</h3>
        <p className="text-secondary small mb-3">{campana.descripcion}</p>

        <div className="mb-3">
          <div className="d-flex justify-content-between small text-secondary mb-1">
            <span>Recaudado</span>
            <span className="fw-bold text-dark">{campana.porcentaje}%</span>
          </div>
          <div className="progress" style={{ height: '8px', backgroundColor: '#e9ecef', borderRadius: '4px' }}>
            <div
              className={`progress-bar ${colorBarra}`}
              role="progressbar"
              style={{ width: `${campana.porcentaje}%` }}
              aria-valuenow={campana.porcentaje}
              aria-valuemin={0}
              aria-valuemax={100}
            />
          </div>
        </div>

        <ul className="list-unstyled text-secondary small mb-4">
          <li>
            <i className="fas fa-map-marker-alt me-2 text-primary" /> {campana.ubicacion}
          </li>
          <li>
            <i className="fas fa-users me-2 text-primary" /> {campana.beneficiarios} beneficiarios
          </li>
          <li>
            <i className="fas fa-donate me-2 text-primary" /> Meta: S/ {formatearEntero(campana.meta)}
          </li>
        </ul>

        <div className="d-flex gap-2 mt-auto">
          <button
            type="button"
            className="btn btn-primary flex-fill"
            onClick={() => onDonar(campana)}
          >
            Donar
          </button>

          {esAdmin && (
            <button
              type="button"
              className="btn btn-danger"
              onClick={() => onEliminar(campana)}
            >
              Eliminar
            </button>
          )}
        </div>
      </div>
    </div>
  )
}
