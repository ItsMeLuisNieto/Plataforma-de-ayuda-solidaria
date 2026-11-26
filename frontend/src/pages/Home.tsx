import { Link } from 'react-router-dom'
import { TestimonialCarousel } from '../components/TestimonialCarousel'

export default function Home() {
  return (
    <>
      <section className="bg-light border-top py-4">
        <div className="container">
          <div className="row align-items-center">
            <div className="col-lg-6">
              <h1 className="display-6 fw-bold text-dark mb-4">¡¡ÚNETE A LA KALLPA SOLIDARIA!!</h1>
              <h3 className="display-7 mb-4" style={{ fontFamily: 'Arial, Helvetica, sans-serif' }}>
                Lleva esperanza a las personas que necesiten
              </h3>
              <p
                className="text-secondary fs-6 mb-4"
                style={{
                  fontFamily: "'Gill Sans', 'Gill Sans MT', Calibri, 'Trebuchet MS', sans-serif",
                }}
              >
                Kallpa Solidary asegura que tus contribuciones impacten directamente a aquellos en
                crisis. Proporcionamos transparencia radical en nuestra logística, para que sepas
                exactamente a dónde va tu ayuda.
              </p>
              <div>
                <Link to="/campanas" className="btn btn-outline-danger btn-lg px-4">
                  Donar ahora
                </Link>
              </div>
            </div>
            <div className="col-lg-6">
              <div className="position-relative">
                <img
                  src="https://policiaamigo.com/wp-content/uploads/2019/03/SML6006.jpg"
                  alt="WE ARE THE WORLD, WE ARE THE CHILDREN :)"
                  className="img-fluid rounded-4 shadow w-100"
                  style={{ maxHeight: '420px', objectFit: 'cover' }}
                />
              </div>
            </div>
          </div>
        </div>
      </section>

      <section className="bg-light border-top py-4 mt-2">
        <div className="container py-4">
          <div className="text-center mb-5">
            <h2 className="fw-bold text-dark display-6 mb-2">Ser parte de Kallpa Solidary significa</h2>
            <h3 className="fw-bold text-primary fs-3">que puedes ayudar de estas tres formas:</h3>
          </div>

          <div className="row g-4 justify-content-center">
            <div className="col-md-6 col-lg-4">
              <div className="card h-100 border-5 shadow-sm rounded-5 text-center p-4 d-flex flex-column justify-content-between">
                <div className="card-body p-0 mb-4">
                  <h4 className="card-title text-dark fw-normal mb-1">Quiero ser</h4>
                  <h3 className="fw-bold text-success mb-4">voluntario</h3>
                  <p className="card-text text-secondary lh-base">
                    Te conectamos con programas de voluntariado en diversas organizaciones sociales.
                    <br />
                    <mark>
                      <strong className="text-dark mark">¡Elige la causa ideal para ti!</strong>
                    </mark>
                  </p>
                </div>
                <div>
                  <Link className="btn btn-primary text-white" to="/campanas">
                    Ver Voluntariados
                  </Link>
                </div>
              </div>
            </div>

            <div className="col-md-6 col-lg-4">
              <div className="card h-100 border-5 shadow-sm rounded-5 text-center p-4 d-flex flex-column justify-content-between">
                <div className="card-body p-0 mb-4">
                  <h4 className="card-title text-dark fw-normal mb-1">Quiero </h4>
                  <h3 className="fw-bold text-success mb-4">donar bienes</h3>
                  <p className="card-text text-secondary lh-base">
                    Conoce las campañas de donación de ropa, alimentos y víveres a través de nuestro
                    directorio social.
                    <br />
                    <mark>
                      <strong className="text-dark mark">Conéctate y dona en simples pasos.</strong>
                    </mark>
                  </p>
                </div>
                <div>
                  <Link className="btn btn-primary text-white" to="/campanas#events">
                    Ver Donaciones
                  </Link>
                </div>
              </div>
            </div>

            <div className="col-md-6 col-lg-4">
              <div className="card h-100 border-5 shadow-sm rounded-5 text-center p-4 d-flex flex-column justify-content-between">
                <div className="card-body p-0 mb-4">
                  <h4 className="card-title text-dark fw-normal mb-1">Quiero </h4>
                  <h3 className="fw-bold text-success mb-4">donar dinero</h3>
                  <p className="card-text text-secondary lh-base">
                    Realiza donaciones monetarias directas y transparentes. Ningún aporte es pequeño,
                    <br />
                    <mark>
                      <strong className="text-dark mark">ya que la unión hace la fuerza.</strong>
                    </mark>
                  </p>
                </div>
                <div>
                  <Link className="btn btn-primary text-white" to="/campanas#campaigns">
                    Ver Campañas
                  </Link>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <TestimonialCarousel />
    </>
  )
}
