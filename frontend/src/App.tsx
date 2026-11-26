import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { Layout } from './components/Layout'
import { RutaAdmin } from './components/RutaAdmin'
import { AuthProvider } from './context/AuthProvider'
import About from './pages/About'
import Campanas from './pages/Campanas'
import Donaciones from './pages/Donaciones'
import Home from './pages/Home'
import Login from './pages/Login'
import NotFound from './pages/NotFound'
import Registro from './pages/Registro'
import Reportes from './pages/Reportes'

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route element={<Layout />}>
            <Route index element={<Home />} />
            <Route path="kallpa-solidary" element={<Navigate to="/" replace />} />
            <Route path="login" element={<Login />} />
            <Route path="registro" element={<Registro />} />
            <Route path="sobre-nosotros" element={<About />} />
            <Route path="campanas" element={<Campanas />} />
            <Route path="donar-ahora" element={<Navigate to="/campanas" replace />} />
            <Route
              path="donaciones"
              element={
                <RutaAdmin>
                  <Donaciones />
                </RutaAdmin>
              }
            />
            <Route
              path="reportes"
              element={
                <RutaAdmin>
                  <Reportes />
                </RutaAdmin>
              }
            />
            <Route path="*" element={<NotFound />} />
          </Route>
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  )
}
