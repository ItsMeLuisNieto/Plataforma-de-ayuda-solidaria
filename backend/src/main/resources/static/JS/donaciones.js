document.addEventListener("DOMContentLoaded", function () {
    const tbody = document.getElementById("tabla-donaciones");
    const csrfToken = document.getElementById("csrf-token")?.content;
    const csrfHeader = document.getElementById("csrf-header")?.content || "X-CSRF-TOKEN";
    let recepcionIdSeleccionado = null;

    const modalRechazoEl = document.getElementById("modalRechazo");
    let modalRechazo = null;
    if (modalRechazoEl && window.bootstrap) {
        modalRechazo = new bootstrap.Modal(modalRechazoEl);
    }

    function getHeaders() {
        const headers = { "Content-Type": "application/json" };
        if (csrfToken) {
            headers[csrfHeader] = csrfToken;
        }
        return headers;
    }

    function cargarRecepciones() {
        fetch('/api/recepcion-donaciones')
            .then(response => response.json())
            .then(data => {
                tbody.innerHTML = "";

                if (!data || data.length === 0) {
                    tbody.innerHTML = `<tr><td colspan="9" class="text-center text-muted py-4">No hay donaciones registradas todavía.</td></tr>`;
                    return;
                }

                data.forEach(recepcion => {
                    const donacion = recepcion.donacion || {};
                    const campanaTitulo = recepcion.campanaNombre || (recepcion.campana ? (recepcion.campana.nombre || recepcion.campana.titulo) : (donacion.campana ? (donacion.campana.nombre || donacion.campana.titulo) : 'N/A'));
                    const codigoLote = donacion.codigoLote || '-';
                    const nombreDonante = donacion.nombreDonante || '-';
                    const producto = donacion.producto || '-';
                    const cantidad = donacion.cantidad || '-';
                    const estado = recepcion.estado || donacion.estado || '-';
                    const fechaSolicitud = recepcion.fechaSolicitud ? recepcion.fechaSolicitud.replace('T', ' ').substring(0, 19) : (donacion.fechaIngreso ? donacion.fechaIngreso.replace('T', ' ').substring(0, 19) : '');

                    let badgeEstado = 'bg-secondary';
                    if (estado === 'PENDIENTE') badgeEstado = 'bg-warning text-dark';
                    else if (estado === 'CONFIRMADO') badgeEstado = 'bg-success';
                    else if (estado === 'RECHAZADO') badgeEstado = 'bg-danger';

                    let acciones = '-';
                    if (estado === 'PENDIENTE') {
                        acciones = `
                            <div class="d-flex gap-2">
                                <button class="btn btn-success btn-sm" data-action="confirmar" data-id="${recepcion.id}">Confirmar</button>
                                <button class="btn btn-outline-danger btn-sm" data-action="rechazar" data-id="${recepcion.id}">Rechazar</button>
                            </div>
                        `;
                    }

                    const fila = `
                        <tr>
                            <td class="fw-bold">${recepcion.id}</td>
                            <td><span class="badge bg-secondary font-monospace">${codigoLote}</span></td>
                            <td><span class="badge bg-primary-subtle text-primary border">${campanaTitulo}</span></td>
                            <td>${nombreDonante}</td>
                            <td>${producto}</td>
                            <td><span class="fw-semibold text-dark">${cantidad}</span></td>
                            <td><span class="badge ${badgeEstado}">${estado}</span></td>
                            <td>${fechaSolicitud}</td>
                            <td>${acciones}</td>
                        </tr>
                    `;
                    tbody.innerHTML += fila;
                });
            })
            .catch(error => {
                console.error("Error al cargar las recepciones:", error);
                tbody.innerHTML = `<tr><td colspan="9" class="text-center text-danger py-4">Error al conectar con el servidor.</td></tr>`;
            });
    }

    tbody.addEventListener("click", function (e) {
        const btn = e.target.closest("button[data-action]");
        if (!btn) return;
        const id = btn.getAttribute("data-id");
        const action = btn.getAttribute("data-action");

        if (action === "confirmar") {
            fetch(`/api/recepcion-donaciones/${id}/confirmar`, {
                method: "POST",
                headers: getHeaders()
            })
                .then(res => res.json())
                .then(() => cargarRecepciones())
                .catch(err => console.error(err));
        } else if (action === "rechazar") {
            recepcionIdSeleccionado = id;
            const motivoInput = document.getElementById("motivoRechazo");
            if (motivoInput) motivoInput.value = "";
            if (modalRechazo) {
                modalRechazo.show();
            }
        }
    });

    const btnConfirmarRechazo = document.getElementById("btnConfirmarRechazo");
    if (btnConfirmarRechazo) {
        btnConfirmarRechazo.addEventListener("click", function () {
            if (!recepcionIdSeleccionado) return;
            const motivo = document.getElementById("motivoRechazo")?.value || "";
            fetch(`/api/recepcion-donaciones/${recepcionIdSeleccionado}/rechazar`, {
                method: "POST",
                headers: getHeaders(),
                body: JSON.stringify({ motivo: motivo })
            })
                .then(res => res.json())
                .then(() => {
                    if (modalRechazo) modalRechazo.hide();
                    cargarRecepciones();
                })
                .catch(err => console.error(err));
        });
    }

    cargarRecepciones();
});
