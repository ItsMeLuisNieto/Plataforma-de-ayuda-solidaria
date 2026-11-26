export interface Evento {
  fecha: string
  hora: string
  titulo: string
  descripcion: string
  campana: string
  lugar: string
}

interface EventCardProps {
  evento: Evento
  onInfo: (campana: string) => void
}

export function EventCard({ evento, onInfo }: EventCardProps) {
  return (
    <div className="col-md-6 col-xl-4">
      <div className="event-card card h-100 border-0 shadow-sm rounded-4 p-4">
        <div className="d-flex justify-content-between align-items-center mb-3">
          <span className="badge bg-primary-subtle text-primary fw-semibold">{evento.fecha}</span>
          <span className="small text-secondary">{evento.hora}</span>
        </div>

        <h4 className="fw-bold text-dark mb-2">{evento.titulo}</h4>
        <p className="text-secondary mb-3">{evento.descripcion}</p>

        <ul className="list-unstyled small text-secondary mb-4">
          <li>
            <i className="fas fa-clipboard-check me-2 text-success" /> Campaña: {evento.campana}
          </li>
          <li>
            <i className="fas fa-map-marker-alt me-2 text-primary" /> {evento.lugar}
          </li>
        </ul>

        <button
          type="button"
          className="btn btn-outline-primary w-100"
          onClick={() => onInfo(evento.campana)}
        >
          Más información
        </button>
      </div>
    </div>
  )
}
