import { useCallback, useEffect, useRef, useState } from 'react'
import type { FormEvent } from 'react'
import { Link, useNavigate, useSearchParams } from 'react-router-dom'
import { useAuth } from '../context/authContext'
import { apiFetch, invalidarCsrf } from '../lib/api'
import { loadGoogleIdentityScript, renderGoogleButton } from '../lib/googleAuth'
import type { GoogleCredentialResponse } from '../lib/googleAuth'

const ERRORES: Record<string, string> = {
  pass_mismatch: 'Las contraseñas no coinciden o son inválidas',
  email_exists: 'El correo electrónico ya existe',
  nombre_exists: 'El nombre de usuario ya está registrado',
  null_email: 'El email no es válido',
  invalid_name: 'Ingresa un nombre válido',
}

function mensajeParaError(codigo: string | null): string {
  if (!codigo) {
    return ''
  }
  return ERRORES[codigo] ?? 'No se pudo completar el registro.'
}

export default function Registro() {
  const [params] = useSearchParams()
  const navigate = useNavigate()
  const { recargar } = useAuth()
  const botonGoogleRef = useRef<HTMLDivElement>(null)

  const desdeGoogle = params.get('on') === 'Google'
  const emailInicial = params.get('email') ?? ''
  const nombreInicial = params.get('nombre') ?? ''

  const [nombre, setNombre] = useState(nombreInicial)
  const [email, setEmail] = useState(emailInicial)
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [error, setError] = useState(mensajeParaError(params.get('error')))
  const [enviando, setEnviando] = useState(false)

  const handleGoogle = useCallback(
    async (response: GoogleCredentialResponse) => {
      try {
        const respuesta = await apiFetch('/login/google', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ token: response.credential }),
        })
        if (!respuesta.ok) {
          throw new Error('Hubo un problema con la autenticación.')
        }

        const datos = (await respuesta.json()) as {
          accion?: string
          email?: string
          nombre?: string
        }

        if (datos.accion === 'completar_registro') {
          navigate(
            `/registro?on=Google&email=${encodeURIComponent(datos.email ?? '')}&nombre=${encodeURIComponent(datos.nombre ?? '')}`,
          )
        } else if (datos.accion === 'login_exitoso') {
          invalidarCsrf()
          await recargar()
          navigate('/campanas')
        } else {
          setError('Hubo un problema con la autenticación.')
        }
      } catch {
        setError('Hubo un problema con la autenticación.')
      }
    },
    [navigate, recargar],
  )

  useEffect(() => {
    let activo = true

    const inicializar = () => {
      if (activo && botonGoogleRef.current) {
        renderGoogleButton(botonGoogleRef.current, 'signup_with', (respuesta) => {
          void handleGoogle(respuesta)
        })
      }
    }

    loadGoogleIdentityScript(inicializar)
    return () => {
      activo = false
    }
  }, [handleGoogle])

  const handleSubmit = async (evento: FormEvent<HTMLFormElement>) => {
    evento.preventDefault()
    setEnviando(true)
    setError('')

    try {
      const respuesta = await apiFetch('/registrar', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({ nombre, email, password, confirmPassword }),
      })

      const codigoError = new URL(respuesta.url).searchParams.get('error')
      if (codigoError) {
        setError(mensajeParaError(codigoError))
        return
      }
      invalidarCsrf()
      navigate('/login')
    } catch {
      setError('No se pudo conectar con el servidor.')
    } finally {
      setEnviando(false)
    }
  }

  return (
    <>
      <div className="login-background" />

      <div className="container auth-container">
        <h1 className="fw-bold">
          {desdeGoogle ? 'Registrarse con Google' : '¡Bienvenido a Kallpa Solidary!'}
        </h1>
        <p>Tu granito de arena puede marcar la diferencia el día de hoy</p>

        <form onSubmit={(evento) => void handleSubmit(evento)}>
          {error && <div className="msg-error mb-3">{error}</div>}

          <label htmlFor="nombre">Nombre</label>
          <input
            type="text"
            id="nombre"
            name="nombre"
            placeholder="Ingrese su nombre"
            value={nombre}
            onChange={(evento) => setNombre(evento.target.value)}
          />

          <label htmlFor="reg-email">Correo</label>
          <input
            type="email"
            id="reg-email"
            name="email"
            placeholder="Ingrese su correo"
            value={email}
            readOnly={desdeGoogle}
            onChange={(evento) => setEmail(evento.target.value)}
          />

          <label htmlFor="reg-pass">Contraseña</label>
          <input
            type="password"
            id="reg-pass"
            name="password"
            placeholder="Ingrese su contraseña"
            value={password}
            onChange={(evento) => setPassword(evento.target.value)}
          />

          <label htmlFor="confirmar-pass">Confirmar contraseña</label>
          <input
            type="password"
            id="confirmar-pass"
            name="confirmPassword"
            placeholder="Repita su contraseña"
            value={confirmPassword}
            onChange={(evento) => setConfirmPassword(evento.target.value)}
          />

          <button type="submit" className="btn btn-primary" disabled={enviando}>
            {enviando ? 'Registrando...' : 'Registrarse'}
          </button>

          <div id="googleButtonDiv" ref={botonGoogleRef} />

          <p className="text-black mt-3 fs-6">
            ¿Ya tienes cuenta? <Link to="/login">Inicia sesión aquí</Link>
          </p>
        </form>
      </div>
    </>
  )
}
