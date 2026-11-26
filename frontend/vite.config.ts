import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'
import type { ProxyOptions } from 'vite'

const backend = 'http://localhost:8090'

type ProxyServer = Parameters<NonNullable<ProxyOptions['configure']>>[0]

/** Spring Security genera URLs absolutas; las convertimos en relativas para que el SPA no salga de Vite. */
function reescribirRedirecciones(proxy: ProxyServer): void {
  proxy.on('proxyRes', (proxyRes) => {
    const ubicacion = proxyRes.headers.location
    if (typeof ubicacion !== 'string') {
      return
    }

    try {
      const destino = new URL(ubicacion, backend)
      if (destino.origin === backend) {
        proxyRes.headers.location = `${destino.pathname}${destino.search}${destino.hash}`
      }
    } catch {
      // La cabecera no era una URL válida; se deja intacta.
    }
  })
}

// https://vite.dev/config/
export default defineConfig({
  devtools: {
    // Remove this `apply` property to enable devtools for build as well
    apply: 'serve',
  },
  plugins: [react()],
  server: {
    proxy: {
      '/api': {
        target: backend,
        changeOrigin: true,
        configure: reescribirRedirecciones,
      },
      '/csrf': {
        target: backend,
        changeOrigin: true,
        configure: reescribirRedirecciones,
      },
      // GET /login debe seguir siendo manejado por React Router;
      // solo el POST (formulario y Google) se envía a Spring Security.
      '/login': {
        target: backend,
        changeOrigin: true,
        configure: reescribirRedirecciones,
        bypass: (req) => (req.method === 'GET' ? req.url ?? '/login' : undefined),
      },
      '/registrar': {
        target: backend,
        changeOrigin: true,
        configure: reescribirRedirecciones,
      },
      '/logout': {
        target: backend,
        changeOrigin: true,
        configure: reescribirRedirecciones,
      },
      '/reportes/exportar': {
        target: backend,
        changeOrigin: true,
        configure: reescribirRedirecciones,
      },
    },
  },
})
