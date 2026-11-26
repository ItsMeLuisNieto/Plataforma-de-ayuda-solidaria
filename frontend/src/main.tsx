import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import './assets/css/responsive.css'
import './assets/css/home-estilos.css'
import './assets/css/about-estilos.css'
import './assets/css/donacion-estilos.css'
import './assets/css/login-estilos.css'
import App from './App'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
