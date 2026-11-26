import type { MouseEvent, ReactNode } from 'react'

interface ModalProps {
  show: boolean
  titulo: ReactNode
  onClose: () => void
  children: ReactNode
  cabeceraClase?: string
  tamano?: '' | 'modal-lg' | 'modal-sm'
}

export function Modal({
  show,
  titulo,
  onClose,
  children,
  cabeceraClase = '',
  tamano = '',
}: ModalProps) {
  if (!show) {
    return null
  }

  const evitarCierre = (evento: MouseEvent<HTMLDivElement>) => evento.stopPropagation()

  return (
    <div
      className="modal d-block"
      role="dialog"
      aria-modal="true"
      style={{ backgroundColor: 'rgba(0, 0, 0, 0.55)' }}
      onClick={onClose}
    >
      <div className={`modal-dialog modal-dialog-centered ${tamano}`} onClick={evitarCierre}>
        <div className="modal-content rounded-4 border-0 shadow">
          <div className={`modal-header ${cabeceraClase}`}>
            <h5 className="modal-title fw-bold">{titulo}</h5>
            <button type="button" className="btn-close" aria-label="Cerrar" onClick={onClose} />
          </div>
          <div className="modal-body">{children}</div>
        </div>
      </div>
    </div>
  )
}
