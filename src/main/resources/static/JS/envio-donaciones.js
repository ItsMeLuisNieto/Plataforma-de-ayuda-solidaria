document.addEventListener("DOMContentLoaded", function () {
    const tbody = document.getElementById("tabla-envios");
    const csrfToken = document.getElementById("csrf-token")?.content;
    const csrfHeader = document.getElementById("csrf-header")?.content || "X-CSRF-TOKEN";

    function getHeaders() {
        const headers = { "Content-Type": "application/json" };
        if (csrfToken) {
            headers[csrfHeader] = csrfToken;
        }
        return headers;
    }

    function formatearFecha(fecha) {
        return fecha ? fecha.replace('T', ' ').substring(0, 19) : '-';
    }

    function cargarEnvios() {
        fetch('/api/envio-donaciones')
            .then(response => {
                if (!response.ok) throw new Error("Error al cargar envíos");
                return response.json();
            })
            .then(data => {
                tbody.innerHTML = "";

                if (!data || data.length === 0) {
                    tbody.innerHTML = `<tr><td colspan="11" class="text-center text-muted py-4">No hay envíos de donaciones registrados todavía.</td></tr>`;
                    return;
                }

                data.forEach(envio => {
                    const donacion = envio.donacion || {};
                    const codigoLote = envio.codigoLote || donacion.codigoLote || '-';
                    const campanaDestino = envio.campanaNombre || (envio.campana ? (envio.campana.nombre || envio.campana.titulo) : 'N/A');
                    const nombreDonante = donacion.nombreDonante || '-';
                    const producto = donacion.producto || '-';
                    const cantidad = donacion.cantidad || '-';
                    const estado = envio.estado || '-';
                    const fechaRegistro = formatearFecha(envio.fechaRegistro);
                    const fechaEnvio = formatearFecha(envio.fechaEnvio);
                    const enviadoPor = envio.enviadoPor || '-';

                    let badgeEstado = 'bg-secondary';
                    if (estado === 'PENDIENTE') badgeEstado = 'bg-warning text-dark';
                    else if (estado === 'ENVIADO') badgeEstado = 'bg-success';

                    let acciones = '-';
                    if (estado === 'PENDIENTE') {
                        acciones = `
                            <button class="btn btn-success btn-sm" data-action="enviar" data-id="${envio.id}">
                                <i class="fas fa-truck me-1"></i>Enviar
                            </button>
                        `;
                    }

                    const fila = `
                        <tr>
                            <td class="fw-bold">${envio.id}</td>
                            <td><span class="badge bg-secondary font-monospace">${codigoLote}</span></td>
                            <td><span class="badge bg-primary-subtle text-primary border">${campanaDestino}</span></td>
                            <td>${nombreDonante}</td>
                            <td>${producto}</td>
                            <td><span class="fw-semibold text-dark">${cantidad}</span></td>
                            <td><span class="badge ${badgeEstado}">${estado}</span></td>
                            <td>${fechaRegistro}</td>
                            <td>${fechaEnvio}</td>
                            <td>${enviadoPor}</td>
                            <td>${acciones}</td>
                        </tr>
                    `;
                    tbody.innerHTML += fila;
                });
            })
            .catch(error => {
                console.error("Error al cargar los envíos:", error);
                tbody.innerHTML = `<tr><td colspan="11" class="text-center text-danger py-4">Error al conectar con el servidor.</td></tr>`;
            });
    }

    tbody.addEventListener("click", function (e) {
        const btn = e.target.closest("button[data-action]");
        if (!btn) return;
        const id = btn.getAttribute("data-id");
        const action = btn.getAttribute("data-action");

        if (action === "enviar") {
            btn.disabled = true;
            fetch(`/api/envio-donaciones/${id}/enviar`, {
                method: "POST",
                headers: getHeaders()
            })
                .then(res => {
                    if (!res.ok) throw new Error("No se pudo enviar la donación");
                    return res.json();
                })
                .then(() => cargarEnvios())
                .catch(err => {
                    console.error(err);
                    btn.disabled = false;
                });
        }
    });

    cargarEnvios();
});
