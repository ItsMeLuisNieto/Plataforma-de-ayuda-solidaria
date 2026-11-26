import { useCallback, useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { CampaignCard } from '../components/CampaignCard'
import { EventCard } from '../components/EventCard'
import type { Evento } from '../components/EventCard'
import { Modal } from '../components/Modal'
import { useAuth } from '../context/authContext'
import { apiFetch, fetchJson, mensajeError } from '../lib/api'
import { tituloCampana } from '../lib/format'
import type { Campana } from '../types'

type VentanaModal =
  | 'donacion'
  | 'dinero'
  | 'objetos'
  | 'informacion'
  | 'copiado'
  | 'crear'
  | null

const EVENTOS: Evento[] = [
  {
    fecha: '12 de septiembre',
    hora: '9:00 AM',
    titulo: 'Jornada de recolección',
    descripcion: 'Recogida de alimentos, ropa y productos de higiene en la plaza principal.',
    campana: 'Canasta de alimentos',
    lugar: 'Parque Kennedy',
  },
  {
    fecha: '15 de septiembre',
    hora: '11:00 AM',
    titulo: 'Mercado solidario',
    descripcion: 'Intercambio y donación de prendas, calzado y materiales para invierno.',
    campana: 'Ropa y abrigo',
    lugar: 'Centro comunitario',
  },
  {
    fecha: '18 de septiembre',
    hora: '3:00 PM',
    titulo: 'Taller de apoyo escolar',
    descripcion: 'Entrega de cuadernos, lapiceros y material escolar a los estudiantes.',
    campana: 'Material escolar',
    lugar: 'Biblioteca municipal',
  },
  {
    fecha: '22 de septiembre',
    hora: '10:00 AM',
    titulo: 'Entrega de paquetes',
    descripcion: 'Distribución de kits con alimentos y productos de aseo para familias necesitadas.',
    campana: 'Canasta de alimentos',
    lugar: 'Sector A y B',
  },
  {
    fecha: '24 de septiembre',
    hora: '1:00 PM',
    titulo: 'Donación de abrigos',
    descripcion: 'Campaña abierta para aportar mantas, chompas y botas para los más vulnerables.',
    campana: 'Ropa y abrigo',
    lugar: 'Calle Las Gardenias',
  },
  {
    fecha: '28 de septiembre',
    hora: '4:00 PM',
    titulo: 'Mochilas para aprender',
    descripcion: 'Fomentamos la educación con útiles, cuadernos y mochilas para niños y jóvenes.',
    campana: 'Material escolar',
    lugar: 'Escuela N° 202',
  },
]

export default function Campanas() {
  const { usuario } = useAuth()
  const esAdmin = usuario?.rol === 'ADMIN'

  const [campanas, setCampanas] = useState<Campana[]>([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState('')
  const [modal, setModal] = useState<VentanaModal>(null)
  const [campanaSeleccionada, setCampanaSeleccionada] = useState<Campana | null>(null)
  const [campanaEvento, setCampanaEvento] = useState('')
  const [metodoPago, setMetodoPago] = useState<'yape' | 'tarjeta'>('yape')
  const [alertaDonacion, setAlertaDonacion] = useState<{ mensaje: string; tipo: string } | null>(null)
  const [enviandoDonacion, setEnviandoDonacion] = useState(false)
  const [enviandoCampana, setEnviandoCampana] = useState(false)
  const [formDonacion, setFormDonacion] = useState({
    campanaId: '',
    nombreDonante: '',
    producto: '',
    cantidad: '',
  })
  const [formCampana, setFormCampana] = useState({
    nombre: '',
    descripcion: '',
    metaDias: '15',
    porcentaje: '0',
    ubicacion: '',
    beneficiarios: '',
    meta: '',
  })

  const cargar = useCallback(async () => {
    try {
      setCargando(true)
      setCampanas(await fetchJson<Campana[]>('/api/campanas'))
      setError('')
    } catch (errorCarga: unknown) {
      setError(errorCarga instanceof Error ? errorCarga.message : 'No se pudieron cargar las campañas.')
    } finally {
      setCargando(false)
    }
  }, [])

  useEffect(() => {
    queueMicrotask(() => {
      void cargar()
    })
  }, [cargar])

  const abrirDonacion = (campana: Campana) => {
    setCampanaSeleccionada(campana)
    setFormDonacion((anterior) => ({
      ...anterior,
      campanaId: String(campana.id),
      nombreDonante: usuario?.nombre ?? anterior.nombreDonante,
    }))
    setAlertaDonacion(null)
    setModal('donacion')
  }

  const eliminarCampana = async (campana: Campana) => {
    if (!window.confirm('¿Quieres eliminar esta campaña?')) {
      return
    }
    try {
      const respuesta = await apiFetch(`/api/campanas/${campana.id}`, { method: 'DELETE' })
      if (!respuesta.ok) {
        throw new Error(await mensajeError(respuesta))
      }
      await cargar()
    } catch (errorEliminar: unknown) {
      setError(errorEliminar instanceof Error ? errorEliminar.message : 'No se pudo eliminar la campaña.')
    }
  }

  const abrirCrear = () => {
    setFormCampana({
      nombre: '',
      descripcion: '',
      metaDias: '15',
      porcentaje: '0',
      ubicacion: '',
      beneficiarios: '',
      meta: '',
    })
    setModal('crear')
  }

  const guardarCampana = async (evento: FormEvent<HTMLFormElement>) => {
    evento.preventDefault()
    setEnviandoCampana(true)
    try {
      const respuesta = await apiFetch('/api/campanas', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          nombre: formCampana.nombre,
          descripcion: formCampana.descripcion,
          metaDias: Number(formCampana.metaDias),
          porcentaje: Number(formCampana.porcentaje),
          ubicacion: formCampana.ubicacion,
          beneficiarios: Number(formCampana.beneficiarios),
          meta: Number(formCampana.meta),
        }),
      })
      if (!respuesta.ok) {
        throw new Error(await mensajeError(respuesta))
      }
      setModal(null)
      await cargar()
    } catch (errorGuardar: unknown) {
      setError(errorGuardar instanceof Error ? errorGuardar.message : 'No se pudo registrar la campaña.')
    } finally {
      setEnviandoCampana(false)
    }
  }

  const guardarDonacion = async (evento: FormEvent<HTMLFormElement>) => {
    evento.preventDefault()
    setAlertaDonacion(null)

    const cantidad = Number(formDonacion.cantidad)
    if (
      !formDonacion.campanaId ||
      !formDonacion.nombreDonante.trim() ||
      !formDonacion.producto.trim() ||
      !cantidad ||
      cantidad <= 0
    ) {
      setAlertaDonacion({ mensaje: 'Revisa los campos obligatorios de la donación.', tipo: 'warning' })
      return
    }

    setEnviandoDonacion(true)
    try {
      const respuesta = await apiFetch('/api/donaciones', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          campanaId: Number(formDonacion.campanaId),
          nombreDonante: formDonacion.nombreDonante.trim(),
          producto: formDonacion.producto.trim(),
          cantidad,
        }),
      })

      if (respuesta.status === 401 || respuesta.status === 403) {
        setAlertaDonacion({
          mensaje: 'Debes iniciar sesión para registrar una donación.',
          tipo: 'warning',
        })
        return
      }

      const datos = (await respuesta.json()) as {
        exito?: boolean
        mensaje?: string
        data?: { codigoLote?: string }
      }
      if (!respuesta.ok || !datos.exito) {
        setAlertaDonacion({
          mensaje: datos.mensaje ?? 'Error al registrar la donación.',
          tipo: 'danger',
        })
        return
      }

      setAlertaDonacion({
        mensaje: `¡Donación registrada con éxito! Código de lote: ${datos.data?.codigoLote ?? ''}`,
        tipo: 'success',
      })
      setFormDonacion((anterior) => ({ ...anterior, producto: '', cantidad: '' }))
      window.setTimeout(() => setModal(null), 2500)
    } catch (errorDonacion: unknown) {
      setAlertaDonacion({
        mensaje: errorDonacion instanceof Error ? errorDonacion.message : 'Error al conectar con el servidor.',
        tipo: 'danger',
      })
    } finally {
      setEnviandoDonacion(false)
    }
  }

  const copiarNumero = async () => {
    try {
      await navigator.clipboard.writeText('929 612 926')
      setModal('copiado')
    } catch {
      setModal('copiado')
    }
  }

  return (
    <main style={{ paddingTop: '10px' }}>
      {error && (
        <div className="container mt-3">
          <div className="alert alert-danger mb-0">{error}</div>
        </div>
      )}

      <section className="container py-5">
        <div className="row align-items-center g-4">
          <div className="col-lg-7">
            <h1 className="display-5 fw-bold text-dark mb-3 section-title">
              Tu apoyo fortalece cada campaña
            </h1>
            <p className="text-secondary fs-5 mb-4">
              Aporta alimentos, artículos de primera necesidad o donaciones monetarias para
              impulsar proyectos concretos en nuestra comunidad.
            </p>
            <div className="d-flex flex-wrap gap-3">
              <a href="#campaigns" className="btn btn-outline-danger btn-lg px-4">
                Dona ya en los eventos disponibles
              </a>
              <a href="#events" className="btn btn-outline-primary btn-lg px-4">
                Ver eventos próximos
              </a>
            </div>
          </div>

          <div className="col-lg-5">
            <div className="card border-0 shadow-lg rounded-4 overflow-hidden">
              <img
                src="https://images.unsplash.com/photo-1488521787991-ed7bbaae773c?auto=format&fit=crop&w=900&q=80"
                alt="Donación"
                className="img-fluid w-100"
                style={{ height: '420px', objectFit: 'cover' }}
              />
            </div>
          </div>
        </div>
      </section>

      <section id="campaigns" className="py-5 bg-light">
        <div className="container">
          <div className="text-center mb-5">
            <h2 className="display-6 fw-bold text-dark section-title">Campañas activas</h2>
            <p className="text-secondary mb-3">
              Cada iniciativa tiene un impacto directo y medible en la comunidad.
            </p>
            {esAdmin && (
              <button type="button" className="btn btn-success px-4 py-3 fw-semibold" onClick={abrirCrear}>
                <i className="fas fa-plus me-2" /> Nueva Campaña
              </button>
            )}
          </div>

          {cargando && (
            <div className="text-center py-5">
              <div className="spinner-border text-primary" role="status">
                <span className="visually-hidden">Cargando campañas...</span>
              </div>
            </div>
          )}

          <div className="row g-4" id="contenedor-campanas">
            {!cargando &&
              campanas.map((campana) => (
                <CampaignCard
                  key={campana.id}
                  campana={campana}
                  esAdmin={esAdmin}
                  onDonar={abrirDonacion}
                  onEliminar={(item) => void eliminarCampana(item)}
                />
              ))}
          </div>

          {!cargando && campanas.length === 0 && (
            <p className="text-center text-muted">No hay campañas registradas todavía.</p>
          )}
        </div>
      </section>

      <section id="events" className="py-5">
        <div className="container">
          <div className="text-center mb-5">
            <h2 className="display-6 fw-bold text-dark section-title">Eventos próximos</h2>
            <p className="text-secondary mb-0">Participa en actividades comunitarias con impacto directo.</p>
          </div>

          <div className="row g-4">
            {EVENTOS.map((evento) => (
              <EventCard
                key={`${evento.fecha}-${evento.titulo}`}
                evento={evento}
                onInfo={(campana) => {
                  setCampanaEvento(campana)
                  setModal('informacion')
                }}
              />
            ))}
          </div>
        </div>
      </section>

      <Modal
        show={modal === 'donacion'}
        titulo={`Selecciona tu tipo de donación${campanaSeleccionada ? `: ${tituloCampana(campanaSeleccionada)}` : ''}`}
        onClose={() => setModal(null)}
      >
        <div className="row g-3">
          <div className="col-12 col-sm-6">
            <button
              type="button"
              className="btn btn-danger w-100 h-100 p-4 rounded-4"
              onClick={() => {
                setMetodoPago('yape')
                setModal('dinero')
              }}
            >
              <i className="fas fa-hand-holding-usd d-block fs-2 mb-2" />
              Donar dinero
            </button>
          </div>
          <div className="col-12 col-sm-6">
            <button
              type="button"
              className="btn btn-success w-100 h-100 p-4 rounded-4"
              onClick={() => {
                setAlertaDonacion(null)
                setModal('objetos')
              }}
            >
              <i className="fas fa-box-open d-block fs-2 mb-2" />
              Donar objetos
            </button>
          </div>
        </div>
      </Modal>

      <Modal
        show={modal === 'dinero'}
        titulo="Donación"
        tamano="modal-lg"
        onClose={() => setModal(null)}
      >
        <div className="container-fluid">
          <div className="row align-items-center g-4">
            <div className="col-lg-5 text-center text-lg-start">
              <h3 className="fw-bold text-uppercase" style={{ color: '#00b4d8' }}>
                DONA A KALLPA
              </h3>
              <p className="fw-semibold mb-1">DE SOL A SOL SE PUEDEN LOGRAR GRANDES COSAS</p>
              <p className="text-secondary small mb-3">Con tu apoyo varias familias estarán agradecidas.</p>
              <p className="fs-6 text-muted">
                Selecciona tu método preferido y realiza tu donación solidaria.
              </p>
            </div>

            <div className="col-lg-7">
              <div
                className="card border-0 text-white mx-auto shadow"
                style={{
                  maxWidth: '410px',
                  background: 'linear-gradient(135deg, #68b0d7, #1072a8)',
                  borderRadius: '25px',
                  padding: '25px',
                }}
              >
                <div className="d-flex gap-2 mb-3">
                  <button
                    type="button"
                    className="btn bg-white fw-bold flex-fill shadow-sm"
                    style={{ color: '#7b2cbf' }}
                    onClick={() => setMetodoPago('yape')}
                  >
                    Yape
                  </button>
                  <button
                    type="button"
                    className="btn bg-white fw-bold flex-fill shadow-sm"
                    style={{ color: '#333' }}
                    onClick={() => setMetodoPago('tarjeta')}
                  >
                    Tarjeta
                  </button>
                </div>

                {metodoPago === 'yape' ? (
                  <div className="text-center">
                    <div className="mb-3">
                      <small className="fw-bold d-block text-white-50">PAGA CON YAPE</small>
                      <h2 className="fw-bold mb-1">929 612 926</h2>
                      <p className="fw-semibold mb-0">Grupo Kallpa Solidary</p>
                    </div>
                    <div className="bg-white rounded-4 p-3 mx-auto shadow-sm" style={{ width: '200px' }}>
                      <img src="/assets/YAPE.png" alt="QR Yape" className="img-fluid" />
                    </div>
                    <button
                      type="button"
                      className="btn btn-outline-light w-100 mt-3 fw-bold"
                      style={{ borderRadius: '10px' }}
                      onClick={() => void copiarNumero()}
                    >
                      <i className="fas fa-copy me-2" /> Copiar número
                    </button>
                  </div>
                ) : (
                  <div>
                    <h5 className="fw-bold mb-3 text-center">PAGO CON TARJETA</h5>
                    <div className="mb-2">
                      <label className="form-label small mb-1">Número de tarjeta</label>
                      <input type="text" className="form-control form-control-sm" placeholder="1234 5678 9012 3456" />
                    </div>
                    <div className="mb-2">
                      <label className="form-label small mb-1">Nombre del titular</label>
                      <input type="text" className="form-control form-control-sm" placeholder="Nombre completo" />
                    </div>
                    <div className="row g-2 mb-3">
                      <div className="col-6">
                        <label className="form-label small mb-1">Vencimiento</label>
                        <input type="text" className="form-control form-control-sm" placeholder="MM/AA" />
                      </div>
                      <div className="col-6">
                        <label className="form-label small mb-1">CVV</label>
                        <input type="password" className="form-control form-control-sm" placeholder="123" />
                      </div>
                    </div>
                    <button type="button" className="btn btn-light w-100 fw-bold text-dark">
                      Realizar donación
                    </button>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      </Modal>

      <Modal
        show={modal === 'copiado'}
        titulo=""
        tamano="modal-sm"
        onClose={() => setModal(null)}
      >
        <div className="text-center">
          <i className="fas fa-check-circle text-success fs-1 mb-2" />
          <h6 className="fw-bold mb-1">¡Número copiado!</h6>
          <p className="small text-muted mb-3">929 612 926</p>
          <button type="button" className="btn btn-primary btn-sm w-100 rounded-pill" onClick={() => setModal(null)}>
            Aceptar
          </button>
        </div>
      </Modal>

      <Modal
        show={modal === 'objetos'}
        titulo={
          <>
            <i className="fas fa-box-open text-success me-2" /> Donación de objetos
          </>
        }
        onClose={() => setModal(null)}
      >
        {alertaDonacion && (
          <div className={`alert alert-${alertaDonacion.tipo} mb-3`} role="alert">
            {alertaDonacion.mensaje}
          </div>
        )}

        <form onSubmit={(evento) => void guardarDonacion(evento)} noValidate>
          <div className="mb-3">
            <label htmlFor="donacionCampanaId" className="form-label fw-semibold">
              Campaña destino <span className="text-danger">*</span>
            </label>
            <select
              id="donacionCampanaId"
              className="form-select form-select-lg"
              required
              value={formDonacion.campanaId}
              onChange={(evento) =>
                setFormDonacion((anterior) => ({ ...anterior, campanaId: evento.target.value }))
              }
            >
              <option value="" disabled>
                Selecciona una campaña...
              </option>
              {campanas.map((campana) => (
                <option key={campana.id} value={campana.id}>
                  {tituloCampana(campana)}
                </option>
              ))}
            </select>
          </div>

          <div className="mb-3">
            <label htmlFor="donacionNombreDonante" className="form-label fw-semibold">
              Nombre del donante / Empresa <span className="text-danger">*</span>
            </label>
            <input
              type="text"
              id="donacionNombreDonante"
              className="form-control form-control-lg"
              placeholder="Ej: Juan Pérez o Asociación Solidaria"
              required
              maxLength={100}
              value={formDonacion.nombreDonante}
              onChange={(evento) =>
                setFormDonacion((anterior) => ({ ...anterior, nombreDonante: evento.target.value }))
              }
            />
          </div>

          <div className="mb-3">
            <label htmlFor="donacionProducto" className="form-label fw-semibold">
              Producto u objeto a donar <span className="text-danger">*</span>
            </label>
            <input
              type="text"
              id="donacionProducto"
              className="form-control form-control-lg"
              placeholder="Ej: Ropa de abrigo, Arroz en saco, Cuadernos escolares"
              required
              maxLength={100}
              value={formDonacion.producto}
              onChange={(evento) =>
                setFormDonacion((anterior) => ({ ...anterior, producto: evento.target.value }))
              }
            />
          </div>

          <div className="mb-3">
            <label htmlFor="donacionCantidad" className="form-label fw-semibold">
              Cantidad (unidades) <span className="text-danger">*</span>
            </label>
            <input
              type="number"
              id="donacionCantidad"
              className="form-control form-control-lg"
              placeholder="Ej: 50"
              min={1}
              step={1}
              required
              value={formDonacion.cantidad}
              onChange={(evento) =>
                setFormDonacion((anterior) => ({ ...anterior, cantidad: evento.target.value }))
              }
            />
          </div>

          <button type="submit" className="btn btn-success w-100 py-3 fw-bold" disabled={enviandoDonacion}>
            <i className="fas fa-paper-plane me-2" />
            {enviandoDonacion ? 'Registrando...' : 'Registrar Donación'}
          </button>
        </form>
      </Modal>

      <Modal
        show={modal === 'informacion'}
        titulo="Más información"
        onClose={() => setModal(null)}
      >
        <div className="mb-3">
          <span className="badge rounded-pill bg-primary-subtle text-primary px-3 py-2">
            Campaña: {campanaEvento}
          </span>
        </div>
        <p className="text-secondary mb-3">
          Este evento forma parte de una iniciativa solidaria enfocada en apoyar a la comunidad con
          acciones concretas, servicio directo y seguimiento cercano.
        </p>
        <ul className="list-unstyled small text-secondary mb-0">
          <li>
            <i className="fas fa-clock me-2 text-primary" /> Horario: 9:00 AM - 1:00 PM
          </li>
          <li>
            <i className="fas fa-user-friends me-2 text-primary" /> Voluntariado disponible
          </li>
          <li>
            <i className="fas fa-hand-holding-heart me-2 text-primary" /> Necesidades prioritarias:
            alimentos, ropa y materiales educativos
          </li>
        </ul>
      </Modal>

      <Modal
        show={modal === 'crear'}
        titulo="Registrar Nueva Campaña"
        onClose={() => setModal(null)}
      >
        <form onSubmit={(evento) => void guardarCampana(evento)}>
          <div className="mb-2">
            <label htmlFor="campana-nombre" className="form-label small fw-semibold">Título</label>
            <input
              type="text"
              id="campana-nombre"
              className="form-control"
              required
              placeholder="Ej: Abrigo para todos"
              value={formCampana.nombre}
              onChange={(evento) => setFormCampana((anterior) => ({ ...anterior, nombre: evento.target.value }))}
            />
          </div>

          <div className="mb-2">
            <label htmlFor="campana-descripcion" className="form-label small fw-semibold">Descripción</label>
            <textarea
              id="campana-descripcion"
              className="form-control"
              rows={2}
              required
              placeholder="Detalle de la campaña"
              value={formCampana.descripcion}
              onChange={(evento) => setFormCampana((anterior) => ({ ...anterior, descripcion: evento.target.value }))}
            />
          </div>

          <div className="row g-2 mb-2">
            <div className="col-6">
              <label htmlFor="campana-dias" className="form-label small fw-semibold">Duración</label>
              <div className="input-group">
                <input
                  type="number"
                  id="campana-dias"
                  className="form-control"
                  min={1}
                  max={365}
                  placeholder="Ej: 15 días"
                  required
                  value={formCampana.metaDias}
                  onChange={(evento) => setFormCampana((anterior) => ({ ...anterior, metaDias: evento.target.value }))}
                />
                <span className="input-group-text bg-light text-secondary">días</span>
              </div>
            </div>
            <div className="col-6">
              <label htmlFor="campana-porcentaje" className="form-label small fw-semibold">Porcentaje (%)</label>
              <input
                type="number"
                id="campana-porcentaje"
                className="form-control"
                min={0}
                max={100}
                required
                value={formCampana.porcentaje}
                onChange={(evento) => setFormCampana((anterior) => ({ ...anterior, porcentaje: evento.target.value }))}
              />
            </div>
          </div>

          <div className="row g-2 mb-2">
            <div className="col-6">
              <label htmlFor="campana-ubicacion" className="form-label small fw-semibold">Ubicación</label>
              <input
                type="text"
                id="campana-ubicacion"
                className="form-control"
                placeholder="Ej: Los Olivos"
                required
                value={formCampana.ubicacion}
                onChange={(evento) => setFormCampana((anterior) => ({ ...anterior, ubicacion: evento.target.value }))}
              />
            </div>
            <div className="col-6">
              <label htmlFor="campana-beneficiarios" className="form-label small fw-semibold">Beneficiarios</label>
              <input
                type="number"
                id="campana-beneficiarios"
                className="form-control"
                min={1}
                placeholder="Ej: 120"
                required
                value={formCampana.beneficiarios}
                onChange={(evento) => setFormCampana((anterior) => ({ ...anterior, beneficiarios: evento.target.value }))}
              />
            </div>
          </div>

          <div className="mb-3">
            <label htmlFor="campana-meta" className="form-label small fw-semibold">Meta (S/)</label>
            <div className="input-group">
              <span className="input-group-text bg-light text-secondary">S/</span>
              <input
                type="number"
                id="campana-meta"
                className="form-control"
                min={0}
                step={1}
                placeholder="Ej: 10000"
                required
                value={formCampana.meta}
                onChange={(evento) => setFormCampana((anterior) => ({ ...anterior, meta: evento.target.value }))}
              />
            </div>
          </div>

          <button type="submit" className="btn btn-primary w-100 fw-bold" disabled={enviandoCampana}>
            {enviandoCampana ? 'Guardando...' : 'Guardar Campaña'}
          </button>
        </form>
      </Modal>
    </main>
  )
}
