export default function About() {
  return (
    <>
      <section className="hero-bg text-white py-5 bg-info">
        <div className="container py-4">
          <div className="row justify-content-center">
            <div className="col-lg-10 text-center">
              <h1 className="fw-bold display-5 mb-4">
                Transformando la Ayuda Humanitaria con Transparencia y Tecnología
              </h1>
              <p className="fs-5 lead mb-4 px-md-5">
                Conectamos la solidaridad de los donantes con las necesidades reales de las
                comunidades, garantizando que cada insumo llegue a su destino de manera segura y
                trazable.
              </p>
              <hr className="w-25 mx-auto border-2 opacity-100 my-4" />
            </div>
          </div>
        </div>
      </section>

      <section className="py-5">
        <div className="container">
          <div className="row g-4">
            <div className="col-md-6">
              <div className="card h-100 shadow border-0 rounded-4 text-center p-4">
                <div className="mb-3">
                  <i className="fas fa-hand-holding-heart display-4 text-danger" />
                </div>
                <h3 className="fw-bold text-dark mb-3">Misión</h3>
                <p className="text-secondary mb-0">
                  Optimizar la gestión de ayuda humanitaria mediante soluciones tecnológicas
                  accesibles y seguras que permitan a las organizaciones sociales canalizar
                  donaciones, controlar inventarios en tiempo real y garantizar entregas
                  transparentes a poblaciones vulnerables.
                </p>
              </div>
            </div>

            <div className="col-md-6">
              <div className="card h-100 shadow border-0 rounded-4 text-center p-4">
                <div className="mb-3">
                  <i className="fas fa-eye display-4 text-danger" />
                </div>
                <h3 className="fw-bold text-dark mb-3">Visión</h3>
                <p className="text-secondary mb-0">
                  Ser la plataforma web de referencia en la región para la digitalización del sector
                  social, impulsando una red de solidaridad donde cada donación sea auditable,
                  eficiente y con un impacto real en la sociedad.
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section className="py-5 bg-light">
        <div className="container">
          <div className="row align-items-center g-5">
            <div className="col-lg-6">
              <div className="position-relative">
                <img
                  src="/assets/about2.jpg"
                  className="img-fluid rounded-4 shadow-lg"
                  alt="Panel de control de inventarios"
                />
              </div>
            </div>
            <div className="col-lg-6">
              <div className="ps-lg-3">
                <span className="badge bg-info text-white fw-bold px-3 py-2 mb-3">Impacto Social</span>
                <h2 className="fw-bold text-dark mb-4">Nuestra Causa</h2>
                <p className="fs-6 text-secondary lh-base mb-4">
                  Muchas organizaciones sociales enfrentan grandes desafíos operativos: registros en
                  papel, pérdida de control del stock en almacén y falta de trazabilidad en las
                  entregas. Esta desorganización puede provocar desabastecimiento o entregas
                  descoordinadas.
                </p>
                <hr className="my-4" />
                <p className="fs-6 text-dark fw-medium lh-base mb-0">
                  Nuestra plataforma centraliza todo el flujo operativo en un solo lugar. Desde el
                  momento en que un donante entrega un bien, hasta que se asigna a una campaña y se
                  entrega al beneficiario final, la información permanece protegida, actualizada y
                  verificada por nuestro sistema de gestión.
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section className="py-5">
        <div className="container">
          <div className="text-center mb-5">
            <h2 className="fw-bold text-dark">Nuestros Pilares Operativos</h2>
            <p className="text-secondary">Fundamentos tecnológicos de nuestra plataforma</p>
          </div>

          <div className="row g-4">
            <div className="col-md-6 col-lg-3">
              <div className="card h-100 shadow-sm border-0 rounded-4 text-center p-4">
                <div className="mb-3">
                  <i className="fas fa-route display-5 text-info" />
                </div>
                <h5 className="fw-bold text-dark">Trazabilidad Total</h5>
                <p className="small text-secondary mb-0">
                  Seguimiento detallado de cada lote de ayuda desde su recepción en el punto de
                  acopio hasta su destino final.
                </p>
              </div>
            </div>

            <div className="col-md-6 col-lg-3">
              <div className="card h-100 shadow-sm border-0 rounded-4 text-center p-4">
                <div className="mb-3">
                  <i className="fas fa-user-shield display-5 text-info" />
                </div>
                <h5 className="fw-bold text-dark">Seguridad y Privacidad</h5>
                <p className="small text-secondary mb-0">
                  Protección estricta de la información sensible de los beneficiarios mediante
                  control de acceso por roles (RBAC).
                </p>
              </div>
            </div>

            <div className="col-md-6 col-lg-3">
              <div className="card h-100 shadow-sm border-0 rounded-4 text-center p-4">
                <div className="mb-3">
                  <i className="fas fa-boxes display-5 text-info" />
                </div>
                <h5 className="fw-bold text-dark">Gestión de Stock Real</h5>
                <p className="small text-secondary mb-0">
                  Reglas automáticas en backend que previenen descalces de inventario y aseguran la
                  disponibilidad de bienes antes de planificar un despacho.
                </p>
              </div>
            </div>

            <div className="col-md-6 col-lg-3">
              <div className="card h-100 shadow-sm border-0 rounded-4 text-center p-4">
                <div className="mb-3">
                  <i className="fas fa-chart-line display-5 text-info" />
                </div>
                <h5 className="fw-bold text-dark">Transparencia Institucional</h5>
                <p className="small text-secondary mb-0">
                  Visualización clara del avance y metas de cada campaña solidaria para generar
                  confianza en la comunidad de donantes.
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>
    </>
  )
}
