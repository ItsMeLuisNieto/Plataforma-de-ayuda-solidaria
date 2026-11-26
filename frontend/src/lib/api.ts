interface CsrfResponse {
  token: string
  headerName: string
}

let csrfPromise: Promise<CsrfResponse> | null = null

function cargarCsrf(): Promise<CsrfResponse> {
  if (!csrfPromise) {
    csrfPromise = fetch('/csrf', { credentials: 'include' })
      .then(async (respuesta) => {
        if (!respuesta.ok) {
          throw new Error('No se pudo obtener el token CSRF')
        }
        return (await respuesta.json()) as CsrfResponse
      })
      .catch((error: unknown) => {
        csrfPromise = null
        throw error
      })
  }
  return csrfPromise
}

export async function getCsrfHeaders(): Promise<Record<string, string>> {
  const { token, headerName } = await cargarCsrf()
  return { [headerName]: token }
}

/** Descarta el token cacheado tras un cambio de sesión (login/logout). */
export function invalidarCsrf(): void {
  csrfPromise = null
}

export async function apiFetch(url: string, init: RequestInit = {}): Promise<Response> {
  const metodo = (init.method ?? 'GET').toUpperCase()
  const headers = new Headers(init.headers)

  if (['POST', 'PUT', 'PATCH', 'DELETE'].includes(metodo)) {
    const csrf = await getCsrfHeaders()
    Object.entries(csrf).forEach(([nombre, valor]) => headers.set(nombre, valor))
  }

  return fetch(url, { ...init, headers, credentials: 'include' })
}

export async function mensajeError(respuesta: Response): Promise<string> {
  try {
    const data: unknown = await respuesta.json()
    if (typeof data === 'object' && data !== null) {
      const objeto = data as Record<string, unknown>
      const mensaje = objeto.mensaje ?? objeto.error ?? objeto.message
      if (typeof mensaje === 'string') {
        return mensaje
      }
    }
  } catch {
    // La respuesta no era JSON; se usa el mensaje genérico.
  }
  return `Error ${respuesta.status} al conectar con el servidor`
}

export async function fetchJson<T>(url: string, init: RequestInit = {}): Promise<T> {
  const respuesta = await apiFetch(url, init)
  if (!respuesta.ok) {
    throw new Error(await mensajeError(respuesta))
  }
  return (await respuesta.json()) as T
}

export function parametroDeUrl(url: string, nombre: string): string | null {
  return new URL(url, window.location.origin).searchParams.get(nombre)
}
