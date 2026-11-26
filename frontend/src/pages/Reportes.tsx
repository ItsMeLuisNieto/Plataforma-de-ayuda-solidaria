import { useCallback, useEffect, useState } from 'react'
import { Modal } from '../components/Modal'
import { fetchJson } from '../lib/api'
import { formatearFecha, tituloCampana } from '../lib/format'
import type { Campana, DonacionResumen, Kpis, Movimiento } from '../types'

const KPI_VACIO: Kpis = {
  totalMontoRecaudado: 0,
  totalBeneficiariosAtendidos: 0,
  totalBienesStock: 0,
}

export default function Reportes() {
  const [kpis, setKpis] = useState<Kpis>(KPI_VACIO)
  const [movimientos, setMovimientos] = useState<Movimiento[]>([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState('')
  const [filtro, setFiltro] = useState('Todas las campañas')
  const [formato, setFormato] = useState<'excel' | 'pdf' | null>(null)

  const cargar = useCallback(async () => {
    try {
      setCargando(true)
      const [campanas, donaciones] = await Promise.all([
        fetchJson<Campana[]>('/api/campanas'),
        fetchJson<DonacionResumen[]>('/api/donaciones'),
      ])

      const confirmadas = donaciones.filter((donacion) => donacion.estado === 'CONFIRMADO')

      setKpis({
        totalMontoRecaudado: 0,
        totalBeneficiariosAtendidos: campanas.reduce(
          (total, campana) => total + campana.beneficiarios,
          0,
        ),
        totalCampanasActivas: campanas.length,
        totalBienesStock: confirmadas.reduce(
          (total, donacion) => total + (donacion.cantidad ?? 0),
          0,
        ),
      })

      setMovimientos(
        confirmadas
          .map((donacion) => ({
            idTransaccion: donacion.id ?? 0,
            fecha: formatearFecha(donacion.fechaIngreso),
            campanaTitulo: tituloCampana(donacion.campana ?? null),
            distrito: donacion.campana?.ubicacion ?? '-',
            tipoDonacion: 'Bienes',
            valorMonto: `${donacion.cantidad ?? 0} ${
              donacion.cantidad === 1 ? 'unidad' : 'unidades'
            } (${donacion.producto ?? '-'})`,
            estado: donacion.estado ?? '-',
          }))
          .sort((a, b) => b.fecha.localeCompare(a.fecha)),
      )
      setError('')
    } catch (errorCarga: unknown) {
      setError(errorCarga instanceof Error ? errorCarga.message : 'No se pudieron cargar los reportes.')
    } finally {
      setCargando(false)
    }
  }, [])

  useEffect(() => {
    queueMicrotask(() => {
      void cargar()
    })
  }, [cargar])

  const campanasFiltro = Array.from(new Set(movimientos.map((movimiento) => movimiento.campanaTitulo)))
  const movimientosFiltrados =
    filtro === 'Todas las campañas'
      ? movimientos
      : movimientos.filter((movimiento) => movimiento.campanaTitulo === filtro)

  const montoFormateado = Number(kpis.totalMontoRecaudado).toLocaleString('es-PE', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  })

  return (
    <section id="reports" className="py-5 bg-light">
      <div className="container">
        <div className="text-center mb-5">
          <h2 className="display-6 fw-bold text-dark section-title">Reportes y Métricas de Impacto</h2>
          <p className="text-secondary mb-3">
            Monitoreo en tiempo real de donaciones recaudadas, entregas y rendimiento de campañas.
          </p>

          <div className="d-flex justify-content-center gap-2">
            <a
              href="/reportes/exportar/excel"
              className="btn btn-success px-4 py-2 fw-semibold"
              onClick={() => setFormato('excel')}
            >
              <i className="fas fa-file-excel me-2" /> Exportar Excel
            </a>
            <a
              href="/reportes/exportar/pdf"
              className="btn btn-danger px-4 py-2 fw-semibold"
              onClick={() => setFormato('pdf')}
            >
              <i className="fas fa-file-pdf me-2" /> Descargar PDF
            </a>
          </div>
        </div>

        {error && <div className="alert alert-danger">{error}</div>}

        <div className="row g-4 mb-5">
          <div className="col-lg-4 col-md-6">
            <div className="card h-100 border-0 shadow-sm rounded-4 p-4">
              <div className="d-flex justify-content-between align-items-center mb-3">
                <span className="badge bg-success-subtle text-success-emphasis fw-semibold px-3 py-2">
                  Recaudación Total
                </span>
                <i className="fas fa-wallet text-success fs-5" />
              </div>
              <h3 className="fw-bold text-dark mb-1">S/ {montoFormateado}</h3>
              <p className="text-secondary small mb-3">Ingresos acumulados por Yape y tarjetas.</p>
              <div
                className="progress mb-2"
                style={{ height: '8px', backgroundColor: '#e9ecef', borderRadius: '4px' }}
              >
                <div
                  className="progress-bar bg-success"
                  role="progressbar"
                  style={{ width: '75%' }}
                  aria-valuenow={75}
                  aria-valuemin={0}
                  aria-valuemax={100}
                />
              </div>
              <span className="small text-secondary">75% de la meta financiera global</span>
            </div>
          </div>

          <div className="col-lg-4 col-md-6">
            <div className="card h-100 border-0 shadow-sm rounded-4 p-4">
              <div className="d-flex justify-content-between align-items-center mb-3">
                <span className="badge bg-primary-subtle text-primary-emphasis fw-semibold px-3 py-2">
                  Impacto Social
                </span>
                <i className="fas fa-users text-primary fs-5" />
              </div>
              <h3 className="fw-bold text-dark mb-1">
                {kpis.totalBeneficiariosAtendidos} Familias
              </h3>
              <p className="text-secondary small mb-3">Beneficiados directos en campañas activas.</p>
              <ul className="list-unstyled text-secondary small mb-0">
                <li>
                  <i className="fas fa-map-marker-alt me-2 text-primary" /> San Martín de Porres
                </li>
                <li>
                  <i className="fas fa-map-marker-alt me-2 text-primary" /> El Agustino, Surco
                </li>
              </ul>
            </div>
          </div>

          <div className="col-lg-4 col-md-12">
            <div className="card h-100 border-0 shadow-sm rounded-4 p-4">
              <div className="d-flex justify-content-between align-items-center mb-3">
                <span className="badge bg-warning-subtle text-warning-emphasis fw-semibold px-3 py-2">
                  Inventario Especies
                </span>
                <i className="fas fa-boxes text-warning fs-5" />
              </div>
              <h3 className="fw-bold text-dark mb-1">{kpis.totalBienesStock} Artículos</h3>
              <p className="text-secondary small mb-3">Bienes físicos recibidos en almacén.</p>
              <span className="badge bg-light text-dark border">Prendas, mochilas, víveres</span>
            </div>
          </div>
        </div>

        <div className="card border-0 shadow-sm rounded-4 p-4">
          <div className="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
            <h4 className="fw-bold text-dark m-0">Historial de Transacciones y Apoyos</h4>
            <div className="d-flex gap-2">
              <select
                className="form-select form-select-sm rounded-3"
                value={filtro}
                onChange={(evento) => setFiltro(evento.target.value)}
              >
                <option>Todas las campañas</option>
                {campanasFiltro.map((campana) => (
                  <option key={campana}>{campana}</option>
                ))}
              </select>
            </div>
          </div>

          <div className="table-responsive">
            <table className="table table-hover align-middle mb-0">
              <thead className="table-info">
                <tr>
                  <th scope="col" className="text-secondary">ID Transacción</th>
                  <th scope="col" className="text-secondary">Fecha</th>
                  <th scope="col" className="text-secondary">Campaña</th>
                  <th scope="col" className="text-secondary">Ubicación</th>
                  <th scope="col" className="text-secondary">Tipo Donación</th>
                  <th scope="col" className="text-secondary">Monto / Cantidad</th>
                  <th scope="col" className="text-secondary">Estado</th>
                </tr>
              </thead>
              <tbody>
                {cargando && (
                  <tr>
                    <td colSpan={7} className="text-center text-muted py-4">
                      Cargando movimientos...
                    </td>
                  </tr>
                )}

                {!cargando &&
                  movimientosFiltrados.map((movimiento) => (
                    <tr key={movimiento.idTransaccion}>
                      <td className="fw-bold text-dark">#TRX-{movimiento.idTransaccion}</td>
                      <td className="text-secondary">{movimiento.fecha}</td>
                      <td className="fw-semibold text-dark">{movimiento.campanaTitulo}</td>
                      <td className="text-secondary">
                        <i className="fas fa-map-marker-alt me-1 text-primary small" /> {movimiento.distrito}
                      </td>
                      <td className="text-secondary">{movimiento.tipoDonacion}</td>
                      <td className="fw-bold text-dark">{movimiento.valorMonto}</td>
                      <td>
                        <span className="badge bg-success-subtle text-success-emphasis px-3 py-2 fw-semibold">
                          {movimiento.estado}
                        </span>
                      </td>
                    </tr>
                  ))}

                {!cargando && movimientosFiltrados.length === 0 && (
                  <tr>
                    <td colSpan={7} className="text-center text-muted py-4">
                      No hay movimientos registrados todavía.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <Modal
        show={formato !== null}
        titulo="Exportación"
        onClose={() => setFormato(null)}
      >
        <div className="text-center py-4">
          <div className="mb-3">
            <i
              className={`fas ${
                formato === 'pdf' ? 'fa-file-pdf text-danger' : 'fa-file-excel text-success'
              } display-3`}
            />
          </div>
          <h4 className="fw-bold text-dark mb-2">Exportando Reporte</h4>
          <p className="text-secondary small mb-3">
            Se descargará el historial de transacciones e indicadores clave en formato{' '}
            <strong>{formato === 'pdf' ? '.pdf' : '.xlsx'}</strong>.
          </p>
        </div>
        <div className="modal-footer border-0 pt-0 justify-content-center">
          <button type="button" className="btn btn-secondary rounded-3 px-4" onClick={() => setFormato(null)}>
            Cerrar
          </button>
        </div>
      </Modal>
    </section>
  )
}
