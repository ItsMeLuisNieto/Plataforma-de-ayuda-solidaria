export function Footer() {
  return (
    <footer className="bg-dark border-top py-4 mt-5">
      <div className="container d-flex flex-column flex-md-row justify-content-between align-items-start gap-4">
        <div>
          <h6 className="text-info fw-bold mb-1">Kallpa Solidary</h6>
          <p className="small fw-bold fs-6 text-white mb-0">
            © 2026 Kallpa Solidary. Todos los derechos reservados.
          </p>
        </div>

        <nav className="d-flex flex-column gap-1">
          <a className="text-decoration-none text-secondary text-white small" href="#terminos">
            Términos y Condiciones
          </a>
          <a className="text-decoration-none text-secondary text-white small" href="#privacidad">
            Política de Privacidad
          </a>
        </nav>

        <div>
          <h6 className="fw-bold text-info mb-2">Contáctanos:</h6>
          <nav className="d-flex flex-column gap-1 text-secondary small">
            <span className="text-white">Calle Las Gardenias 179, Independencia</span>
            <a className="text-decoration-none text-secondary text-white small" href="tel:+51904399718">
              +51 904 399 718
            </a>
          </nav>
        </div>
      </div>
    </footer>
  )
}
