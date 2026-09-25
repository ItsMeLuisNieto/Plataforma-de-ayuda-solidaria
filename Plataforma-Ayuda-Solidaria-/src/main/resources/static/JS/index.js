

document.addEventListener("DOMContentLoaded", () => {
    // Cargar campañas al cargar la página
    //cargarCampanas();
});

/*function cargarCampanas() {
    fetch("/api/campanas")
        .then(response => {
            if (!response.ok) {
                throw new Error("Error al consultar la API");
            }
            return response.json();
        })
        .then(campanas => {
            const contenedor = document.getElementById("contenedor-campanas");
            if (!contenedor) return;

            contenedor.innerHTML = "";

            campanas.forEach(campana => {
                
                let colorBarra = "bg-success";
                if (campana.porcentaje < 40) {
                    colorBarra = "bg-danger";
                } else if (campana.porcentaje < 75) {
                    colorBarra = "bg-warning";
                }

                const tarjeta = `
                    <div class="col-lg-4">
                        <div class="campaign-card card h-100 border-0 shadow-sm rounded-4 p-4">
                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <span class="badge bg-warning-subtle text-warning-emphasis fw-semibold px-3 py-2">En curso</span>
                                <span class="small text-secondary">${campana.metaDias || 'Por definir'}</span>
                            </div>
                            <h3 class="fw-bold text-dark mb-2">${campana.titulo}</h3>
                            <p class="text-secondary mb-3">${campana.descripcion}</p>
                            <div class="mb-3">
                                <div class="d-flex justify-content-between small text-secondary mb-1">
                                    <span>Recaudado</span>
                                    <span>${campana.porcentaje}%</span>
                                </div>
                                <div class="progress" style="height: 8px;">
                                    <div class="progress-bar ${colorBarra}" role="progressbar" style="width: ${campana.porcentaje}%"></div>
                                </div>
                            </div>
                            <ul class="list-unstyled text-secondary small mb-4">
                                <li><i class="fas fa-map-marker-alt me-2 text-primary"></i>${campana.ubicacion}</li>
                                <li><i class="fas fa-users me-2 text-primary"></i>${campana.beneficiarios}</li>
                                <li><i class="fas fa-donate me-2 text-primary"></i>${campana.meta || 'Meta por definir'}</li>
                            </ul>
                            <div class="d-flex gap-2 mt-auto">
                                <button class="btn btn-primary flex-fill" data-bs-toggle="modal" data-bs-target="#donationModal">
                                    Donar
                                </button>
                            </div>
                        </div>
                    </div>
                `;
                contenedor.innerHTML += tarjeta;
            });
        })
        .catch(error => {
            console.error("Error al cargar campañas:", error);
        });
}
*/