import { useCallback, useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { apiFetch, fetchJson, mensajeError } from '../lib/api'
import type { Testimonio } from '../types'
import { Modal } from './Modal'

const formVacio = { nombre: '', rol: 'Donante', comentario: '' }

export function TestimonialCarousel() {
  const [testimonios, setTestimonios] = useState<Testimonio[]>([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState('')
  const [activo, setActivo] = useState(0)
  const [mostrarCrear, setMostrarCrear] = useState(false)
  const [mostrarEditar, setMostrarEditar] = useState(false)
  const [editando, setEditando] = useState<Testimonio | null>(null)
  const [form, setForm] = useState(formVacio)

  const cargar = useCallback(async () => {
    try {
      setCargando(true)
      const datos = await fetchJson<Testimonio[]>('/api/testimonios')
      setTestimonios(datos)
      setActivo(0)
      setError('')
    } catch (errorCarga: unknown) {
      setError(errorCarga instanceof Error ? errorCarga.message : 'No se pudieron cargar los testimonios.')
    } finally {
      setCargando(false)
    }
  }, [])

  useEffect(() => {
    queueMicrotask(() => {
      void cargar()
    })
  }, [cargar])

  const actualizarCampo = (campo: keyof typeof form, valor: string) => {
    setForm((anterior) => ({ ...anterior, [campo]: valor }))
  }

  const abrirCrear = () => {
    setForm(formVacio)
    setMostrarCrear(true)
  }

  const abrirEditar = (testimonio: Testimonio) => {
    setEditando(testimonio)
    setForm({
      nombre: testimonio.nombre,
      rol: testimonio.rol,
      comentario: testimonio.comentario,
    })
    setMostrarEditar(true)
  }

  const guardarCrear = async (evento: FormEvent<HTMLFormElement>) => {
    evento.preventDefault()
    try {
      const respuesta = await apiFetch('/api/testimonios', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(form),
      })
      if (!respuesta.ok) {
        throw new Error(await mensajeError(respuesta))
      }
      setMostrarCrear(false)
      await cargar()
    } catch (errorGuardar: unknown) {
      setError(errorGuardar instanceof Error ? errorGuardar.message : 'No se pudo guardar el testimonio.')
    }
  }

  const guardarEditar = async (evento: FormEvent<HTMLFormElement>) => {
    evento.preventDefault()
    if (!editando) {
      return
    }
    try {
      const respuesta = await apiFetch(`/api/testimonios/${editando.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(form),
      })
      if (!respuesta.ok) {
        throw new Error(await mensajeError(respuesta))
      }
      setMostrarEditar(false)
      setEditando(null)
      await cargar()
    } catch (errorGuardar: unknown) {
      setError(errorGuardar instanceof Error ? errorGuardar.message : 'No se pudo actualizar el testimonio.')
    }
  }

  const mover = (direccion: 1 | -1) => {
    if (testimonios.length === 0) {
      return
    }
    setActivo((indice) => (indice + direccion + testimonios.length) % testimonios.length)
  }

  const actual = testimonios[activo]

  return (
    <section id="testimonios" className="py-5 bg-light">
      <div className="container text-center">
        <h2 className="fw-bold mb-4">Voces de Nuestra Comunidad</h2>

        <div
          id="carruselTestimonios"
          className="carousel slide shadow rounded p-4 bg-white"
          data-bs-interval="4000"
        >
          <div className="carousel-inner" id="contenedor-testimonios">
            {cargando && (
              <div className="carousel-item active">
                <div className="d-flex justify-content-center py-5">
                  <div className="spinner-border text-primary" role="status">
                    <span className="visually-hidden">Cargando testimonios...</span>
                  </div>
                </div>
              </div>
            )}

            {!cargando && error && (
              <div className="carousel-item active p-4">
                <p className="text-danger">{error}</p>
              </div>
            )}

            {!cargando && !error && testimonios.length === 0 && (
              <div className="carousel-item active p-4">
                <p className="text-muted">Sé el primero en compartir tu testimonio.</p>
              </div>
            )}

            {!cargando &&
              !error &&
              testimonios.map((testimonio, indice) => (
                <div
                  key={testimonio.id}
                  className={`carousel-item ${indice === activo ? 'active' : ''}`}
                >
                  <div className="p-4 text-center">
                    <p className="fs-5 fst-italic text-secondary">"{testimonio.comentario}"</p>
                    <h5 className="fw-bold mb-0 text-dark">{testimonio.nombre}</h5>
                    <small className="text-primary fw-semibold d-block mb-3">{testimonio.rol}</small>
                    <button
                      type="button"
                      className="btn btn-outline-warning btn-sm rounded-pill px-3"
                      onClick={() => abrirEditar(testimonio)}
                    >
                      <i className="bi bi-pencil me-1" /> Editar
                    </button>
                  </div>
                </div>
              ))}
          </div>

          {actual && testimonios.length > 1 && (
            <>
              <button
                type="button"
                className="carousel-control-prev"
                onClick={() => mover(-1)}
                aria-label="Anterior"
              >
                <span className="carousel-control-prev-icon bg-dark rounded-circle" aria-hidden="true" />
              </button>
              <button
                type="button"
                className="carousel-control-next"
                onClick={() => mover(1)}
                aria-label="Siguiente"
              >
                <span className="carousel-control-next-icon bg-dark rounded-circle" aria-hidden="true" />
              </button>
            </>
          )}
        </div>

        <div className="text-center mt-4">
          <p className="mb-2 text-dark fw-bold">¿Has participado como donante o voluntario?</p>
          <button type="button" className="btn btn-info text-white fw-bold shadow-sm" onClick={abrirCrear}>
            <i className="bi bi-chat-square-quote-fill me-1" /> Comparte tu Experiencia
          </button>
        </div>
      </div>

      <Modal
        show={mostrarCrear}
        titulo="Déjanos tu Testimonio"
        cabeceraClase="bg-info text-white"
        onClose={() => setMostrarCrear(false)}
      >
        <form onSubmit={(evento) => void guardarCrear(evento)}>
          <div className="mb-3">
            <label htmlFor="nombre" className="form-label fw-semibold text-dark">
              Nombre completo
            </label>
            <input
              type="text"
              className="form-control"
              id="nombre"
              required
              placeholder="Ej: Jimin Gómez"
              value={form.nombre}
              onChange={(evento) => actualizarCampo('nombre', evento.target.value)}
            />
          </div>
          <div className="mb-3">
            <label htmlFor="rol" className="form-label fw-semibold text-dark">
              ¿Cómo has apoyado?
            </label>
            <select
              className="form-select"
              id="rol"
              required
              value={form.rol}
              onChange={(evento) => actualizarCampo('rol', evento.target.value)}
            >
              <option value="Donante">Donante</option>
              <option value="Voluntario">Voluntario</option>
            </select>
          </div>
          <div className="mb-3">
            <label htmlFor="comentario" className="form-label fw-semibold text-dark">
              Tu Comentario
            </label>
            <textarea
              className="form-control"
              id="comentario"
              rows={3}
              required
              placeholder="Escribe aquí tu experiencia..."
              value={form.comentario}
              onChange={(evento) => actualizarCampo('comentario', evento.target.value)}
            />
          </div>
          <button type="submit" className="btn btn-primary w-100 fw-bold">
            Enviar Testimonio
          </button>
        </form>
      </Modal>

      <Modal
        show={mostrarEditar}
        titulo={
          <>
            <i className="bi bi-pencil-square me-1" /> Editar Testimonio
          </>
        }
        cabeceraClase="bg-warning text-dark"
        onClose={() => {
          setMostrarEditar(false)
          setEditando(null)
        }}
      >
        <form onSubmit={(evento) => void guardarEditar(evento)}>
          <div className="mb-3">
            <label htmlFor="edit-nombre" className="form-label fw-semibold text-dark">
              Nombre completo
            </label>
            <input
              type="text"
              className="form-control"
              id="edit-nombre"
              required
              value={form.nombre}
              onChange={(evento) => actualizarCampo('nombre', evento.target.value)}
            />
          </div>
          <div className="mb-3">
            <label htmlFor="edit-rol" className="form-label fw-semibold text-dark">
              ¿Cómo has apoyado?
            </label>
            <select
              className="form-select"
              id="edit-rol"
              required
              value={form.rol}
              onChange={(evento) => actualizarCampo('rol', evento.target.value)}
            >
              <option value="Donante">Donante</option>
              <option value="Voluntario">Voluntario</option>
            </select>
          </div>
          <div className="mb-3">
            <label htmlFor="edit-comentario" className="form-label fw-semibold text-dark">
              Tu Comentario
            </label>
            <textarea
              className="form-control"
              id="edit-comentario"
              rows={3}
              required
              value={form.comentario}
              onChange={(evento) => actualizarCampo('comentario', evento.target.value)}
            />
          </div>
          <button type="submit" className="btn btn-warning w-100 fw-bold">
            Guardar Cambios
          </button>
        </form>
      </Modal>
    </section>
  )
}
