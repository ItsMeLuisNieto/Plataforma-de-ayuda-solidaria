import { useCallback, useEffect, useRef, useState } from 'react'
import type { FormEvent } from 'react'
import { Link, useNavigate, useSearchParams } from 'react-router-dom'
import { useAuth } from '../context/authContext'
import { apiFetch, invalidarCsrf } from '../lib/api'
import { loadGoogleIdentityScript, renderGoogleButton } from '../lib/googleAuth'
import type { GoogleCredentialResponse } from '../lib/googleAuth'

export default function Login() {
  const [params] = useSearchParams()
  const navigate = useNavigate()
  const { recargar } = useAuth()
  const botonGoogleRef = useRef<HTMLDivElement>(null)

  const [email, setEmail] = useState('')
  const [pass, setPass] = useState('')
  const [error, setError] = useState(
    params.get('error') === 'incorrect_credentials' ? 'Las credenciales no son correctas' : '',
  )
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
        renderGoogleButton(botonGoogleRef.current, 'signin_with', (respuesta) => {
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
      const respuesta = await apiFetch('/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({ email, pass }),
      })

      const url = new URL(respuesta.url)
      if (url.searchParams.get('error') === 'incorrect_credentials') {
        setError('Las credenciales no son correctas')
        return
      }
      invalidarCsrf()
      await recargar()
      navigate('/campanas')
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
        <h1 className="fw-bold">¡Hola de nuevo!</h1>
        <p>Inicia sesión para continuar</p>

        <form onSubmit={(evento) => void handleSubmit(evento)}>
          {error && <div className="msg-error mb-3">{error}</div>}

          <label htmlFor="login-email">Correo electrónico</label>
          <input
            type="email"
            name="email"
            id="login-email"
            placeholder="Tu correo electrónico aquí"
            value={email}
            onChange={(evento) => setEmail(evento.target.value)}
          />

          <label htmlFor="login-password">Contraseña</label>
          <input
            type="password"
            name="pass"
            id="login-password"
            placeholder="Tu contraseña"
            value={pass}
            onChange={(evento) => setPass(evento.target.value)}
          />

          <button type="submit" className="btn btn-primary" disabled={enviando}>
            {enviando ? 'Iniciando sesión...' : 'Iniciar sesión'}
          </button>

          <div id="googleButtonDiv" ref={botonGoogleRef} />

          <p className="text-black mt-3 fs-6">
            ¿No tienes cuenta? <Link to="/registro">Regístrate aquí</Link>
          </p>
        </form>
      </div>
    </>
  )
}
