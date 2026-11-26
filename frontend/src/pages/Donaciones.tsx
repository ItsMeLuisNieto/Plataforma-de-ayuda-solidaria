import { useCallback, useEffect, useState } from 'react'
import { Modal } from '../components/Modal'
import { apiFetch, fetchJson, mensajeError } from '../lib/api'
import { formatearFecha, tituloCampana } from '../lib/format'
import type { RecepcionDonacion } from '../types'

function claseEstado(estado: string): string {
  if (estado === 'PENDIENTE') {
    return 'bg-warning text-dark'
  }
  if (estado === 'CONFIRMADO') {
    return 'bg-success'
  }
  if (estado === 'RECHAZADO') {
    return 'bg-danger'
  }
  return 'bg-secondary'
}

export default function Donaciones() {
  const [recepciones, setRecepciones] = useState<RecepcionDonacion[]>([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState('')
  const [rechazoId, setRechazoId] = useState<number | null>(null)
  const [motivoRechazo, setMotivoRechazo] = useState('')
  const [procesando, setProcesando] = useState(false)

  const cargar = useCallback(async () => {
    try {
      setCargando(true)
      setRecepciones(await fetchJson<RecepcionDonacion[]>('/api/recepcion-donaciones'))
      setError('')
    } catch (errorCarga: unknown) {
      setError(errorCarga instanceof Error ? errorCarga.message : 'No se pudieron cargar las donaciones.')
    } finally {
      setCargando(false)
    }
  }, [])

  useEffect(() => {
    queueMicrotask(() => {
      void cargar()
    })
  }, [cargar])

  const confirmar = async (id: number) => {
    setProcesando(true)
    try {
      const respuesta = await apiFetch(`/api/recepcion-donaciones/${id}/confirmar`, { method: 'POST' })
      if (!respuesta.ok) {
        throw new Error(await mensajeError(respuesta))
      }
      await cargar()
    } catch (errorConfirmar: unknown) {
      setError(errorConfirmar instanceof Error ? errorConfirmar.message : 'No se pudo confirmar la donación.')
    } finally {
      setProcesando(false)
    }
  }

  const abrirRechazo = (id: number) => {
    setRechazoId(id)
    setMotivoRechazo('')
  }

  const confirmarRechazo = async () => {
    if (rechazoId === null) {
      return
    }
    setProcesando(true)
    try {
      const respuesta = await apiFetch(`/api/recepcion-donaciones/${rechazoId}/rechazar`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ motivo: motivoRechazo }),
      })
      if (!respuesta.ok) {
        throw new Error(await mensajeError(respuesta))
      }
      setRechazoId(null)
      await cargar()
    } catch (errorRechazo: unknown) {
      setError(errorRechazo instanceof Error ? errorRechazo.message : 'No se pudo rechazar la donación.')
    } finally {
      setProcesando(false)
    }
  }

  return (
    <main className="container my-5">
      <div className="row mb-4 align-items-center">
        <div className="col">
          <h2 className="text-dark fw-bold mb-1">
            <i className="fas fa-boxes text-info me-2" /> Recepción y Trazabilidad de Donaciones
          </h2>
          <p className="text-muted mb-0">
            Donaciones con estado PENDIENTE llegan aquí para confirmar o rechazar
          </p>
        </div>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      <div className="card border-0 shadow-sm rounded-4 overflow-hidden">
        <div className="card-body p-4 bg-white">
          <div className="table-responsive">
            <table className="table table-hover align-middle mb-0">
              <thead className="table-dark text-uppercase fs-7">
                <tr>
                  <th className="py-3">ID Recepción</th>
                  <th className="py-3">Código de Lote</th>
                  <th className="py-3">Campaña</th>
                  <th className="py-3">Donante</th>
                  <th className="py-3">Producto</th>
                  <th className="py-3">Cantidad</th>
                  <th className="py-3">Estado</th>
                  <th className="py-3">Fecha Solicitud</th>
                  <th className="py-3">Acciones</th>
                </tr>
              </thead>
              <tbody id="tabla-donaciones">
                {cargando && (
                  <tr>
                    <td colSpan={9} className="text-center text-muted py-4">
                      Cargando donaciones...
                    </td>
                  </tr>
                )}

                {!cargando && recepciones.length === 0 && (
                  <tr>
                    <td colSpan={9} className="text-center text-muted py-4">
                      No hay donaciones registradas todavía.
                    </td>
                  </tr>
                )}

                {!cargando &&
                  recepciones.map((recepcion) => {
                    const donacion = recepcion.donacion ?? null
                    const campana = tituloCampana(recepcion.campana ?? donacion?.campana ?? null)
                    const estado = recepcion.estado ?? donacion?.estado ?? '-'
                    const fecha =
                      formatearFecha(recepcion.fechaSolicitud) || formatearFecha(donacion?.fechaIngreso)

                    return (
                      <tr key={recepcion.id}>
                        <td className="fw-bold">{recepcion.id}</td>
                        <td>
                          <span className="badge bg-secondary font-monospace">
                            {donacion?.codigoLote ?? '-'}
                          </span>
                        </td>
                        <td>
                          <span className="badge bg-primary-subtle text-primary border">
                            {recepcion.campanaNombre ?? campana}
                          </span>
                        </td>
                        <td>{donacion?.nombreDonante ?? '-'}</td>
                        <td>{donacion?.producto ?? '-'}</td>
                        <td>
                          <span className="fw-semibold text-dark">{donacion?.cantidad ?? '-'}</span>
                        </td>
                        <td>
                          <span className={`badge ${claseEstado(estado)}`}>{estado}</span>
                        </td>
                        <td>{fecha}</td>
                        <td>
                          {estado === 'PENDIENTE' ? (
                            <div className="d-flex gap-2">
                              <button
                                type="button"
                                className="btn btn-success btn-sm"
                                disabled={procesando}
                                onClick={() => void confirmar(recepcion.id)}
                              >
                                Confirmar
                              </button>
                              <button
                                type="button"
                                className="btn btn-outline-danger btn-sm"
                                onClick={() => abrirRechazo(recepcion.id)}
                              >
                                Rechazar
                              </button>
                            </div>
                          ) : (
                            '-'
                          )}
                        </td>
                      </tr>
                    )
                  })}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <Modal
        show={rechazoId !== null}
        titulo="Rechazar Donación"
        onClose={() => setRechazoId(null)}
      >
        <div className="mb-3">
          <label htmlFor="motivoRechazo" className="form-label fw-semibold">
            Motivo del rechazo (opcional)
          </label>
          <textarea
            id="motivoRechazo"
            className="form-control"
            rows={3}
            placeholder="Especifica el motivo..."
            value={motivoRechazo}
            onChange={(evento) => setMotivoRechazo(evento.target.value)}
          />
        </div>
        <button
          type="button"
          className="btn btn-danger w-100"
          disabled={procesando}
          onClick={() => void confirmarRechazo()}
        >
          Rechazar
        </button>
      </Modal>
    </main>
  )
}
