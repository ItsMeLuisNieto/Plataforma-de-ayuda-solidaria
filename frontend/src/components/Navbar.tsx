import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/authContext'

export function Navbar() {
  const [menuAbierto, setMenuAbierto] = useState(false)
  const [gestionAbierta, setGestionAbierta] = useState(false)
  const { usuario, cerrarSesion } = useAuth()
  const navigate = useNavigate()

  const esAdmin = usuario?.rol === 'ADMIN'
  const cerrarMenu = () => setMenuAbierto(false)

  const handleLogout = async () => {
    await cerrarSesion()
    cerrarMenu()
    navigate('/')
  }

  return (
    <header>
      <nav
        className="navbar navbar-expand-lg navbar-dark border-bottom border-dark bg-primary"
        style={{ padding: '20px' }}
      >
        <div className="container-fluid px-lg-5">
          <Link to="/" className="text-decoration-none" onClick={cerrarMenu}>
            <h1 className="text-white fw-bold mb-0">Kallpa Solidary</h1>
          </Link>

          <button
            className="navbar-toggler"
            type="button"
            onClick={() => setMenuAbierto((abierto) => !abierto)}
            aria-controls="menuNavegacion"
            aria-expanded={menuAbierto}
            aria-label="Toggle navigation"
          >
            <span className="navbar-toggler-icon" />
          </button>

          <div
            className={`collapse navbar-collapse ${menuAbierto ? 'show' : ''}`}
            id="menuNavegacion"
          >
            <ul className="navbar-nav gap-3 ms-auto align-items-center mt-3 mt-lg-0 text-center">
              <li className="nav-item">
                <Link
                  className="nav-link text-white fw-semibold"
                  to="/campanas#campaigns"
                  onClick={cerrarMenu}
                >
                  Campañas
                </Link>
              </li>

              <li className="nav-item">
                <Link
                  className="nav-link text-white fw-semibold"
                  to="/campanas#events"
                  onClick={cerrarMenu}
                >
                  Eventos
                </Link>
              </li>

              <li className="nav-item">
                <Link
                  className="nav-link text-white fw-semibold"
                  to="/sobre-nosotros"
                  onClick={cerrarMenu}
                >
                  Sobre Nosotros
                </Link>
              </li>

              {esAdmin && (
                <li className={`nav-item dropdown ${gestionAbierta ? 'show' : ''}`}>
                  <a
                    className="nav-link active text-white dropdown-toggle fw-semibold"
                    href="#gestion"
                    role="button"
                    aria-expanded={gestionAbierta}
                    onClick={(evento) => {
                      evento.preventDefault()
                      setGestionAbierta((abierta) => !abierta)
                    }}
                  >
                    Gestión
                  </a>
                  <ul className={`dropdown-menu text-center ${gestionAbierta ? 'show' : ''}`}>
                    <li>
                      <Link
                        className="dropdown-item fw-semibold fs-6 py-2"
                        to="/donaciones"
                        onClick={cerrarMenu}
                      >
                        Recepción de Donaciones
                      </Link>
                    </li>
                    <li>
                      <Link
                        className="dropdown-item fw-semibold fs-6 py-2"
                        to="/reportes"
                        onClick={cerrarMenu}
                      >
                        Reportes
                      </Link>
                    </li>
                  </ul>
                </li>
              )}

              {!usuario && (
                <>
                  <li className="nav-item w-100 w-lg-auto me-lg-2 mb-2 mb-lg-0">
                    <Link
                      className="btn btn-outline-light text-nowrap rounded-pill px-4 fw-semibold shadow-sm w-100"
                      to="/login"
                      onClick={cerrarMenu}
                    >
                      Iniciar Sesión
                    </Link>
                  </li>

                  <li className="nav-item w-100 w-lg-auto">
                    <Link
                      className="btn btn-dark text-white text-nowrap rounded-pill px-4 fw-bold shadow w-100"
                      to="/registro"
                      onClick={cerrarMenu}
                    >
                      Registrarse
                    </Link>
                  </li>
                </>
              )}

              {usuario && (
                <li className="nav-item w-100 text-white w-lg-auto d-flex align-items-center gap-3">
                  <span className="text-white fw-bold">¡Hola, {usuario.nombre}!</span>
                  <button
                    type="button"
                    className="btn btn-danger text-white fw-semibold px-3"
                    onClick={() => void handleLogout()}
                  >
                    Cerrar Sesión
                  </button>
                </li>
              )}
            </ul>
          </div>
        </div>
      </nav>
    </header>
  )
}
