let promesaTokenApi = null;

async function asegurarTokenApi() {
  const guardado = sessionStorage.getItem('kallpaJwt');
  if (guardado) {
    return guardado;
  }
  if (!promesaTokenApi) {
    promesaTokenApi = fetch('/auth/token', { credentials: 'same-origin' })
      .then(respuesta => respuesta.ok ? respuesta.json() : null)
      .then(datos => {
        if (datos && datos.token) {
          sessionStorage.setItem('kallpaJwt', datos.token);
        }
        return datos ? datos.token : null;
      })
      .catch(() => null)
      .finally(() => { promesaTokenApi = null; });
  }
  return promesaTokenApi;
}

async function apiFetch(url, opciones = {}) {
  const token = await asegurarTokenApi();
  const cabeceras = new Headers(opciones.headers || {});
  if (token) {
    cabeceras.set('Authorization', 'Bearer ' + token);
  }
  return fetch(url, {
    ...opciones,
    headers: cabeceras,
    credentials: opciones.credentials || 'same-origin'
  });
}

async function apiHeaders(cabeceras = {}) {
  const token = await asegurarTokenApi();
  const resultado = new Headers(cabeceras);
  if (token) {
    resultado.set('Authorization', 'Bearer ' + token);
  }
  return Object.fromEntries(resultado.entries());
}
