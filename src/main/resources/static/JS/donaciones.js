document.addEventListener("DOMContentLoaded", function () {
    const tbody = document.getElementById("tabla-donaciones");

    fetch('/api/donaciones')
        .then(response => response.json())
        .then(data => {
            tbody.innerHTML = ""; // Limpiar tabla

            if (!data || data.length === 0) {
                tbody.innerHTML = `<tr><td colspan="7" class="text-center text-muted py-4">No hay donaciones registradas todavía.</td></tr>`;
                return;
            }

            data.forEach(donacion => {
                // Definir los botones solo si está pendiente de verificación
                let botonesAccion = '';
                if (donacion.estado === 'PENDIENTE_VERIFICACION' || donacion.estado === 'Pendiente') {
                    botonesAccion = `
            <button class="btn btn-success btn-sm me-1" onclick="aprobarDonacion(${donacion.id})">Confirmar</button>
            <button class="btn btn-danger btn-sm" onclick="rechazarDonacion(${donacion.id})">Rechazar</button>
        `;
                } else {
                    botonesAccion = `<span class="text-muted">Verificado</span>`;
                }

                const fila = `
        <tr>
            <td>${donacion.id}</td>
            <td><span class="badge bg-secondary">${donacion.codigoLote || 'N/A'}</span></td>
            <td>${donacion.nombreDonante}</td>
            <td>${donacion.producto || donacion.tipoAyuda || 'Monetaria (Yape)'}</td>
            <td>${donacion.cantidad || 'S/ ' + donacion.monto}</td>
            <td><span class="badge bg-warning text-dark">${donacion.estado}</span></td>
            <td>${donacion.fechaIngreso ? donacion.fechaIngreso.replace('T', ' ').substring(0, 16) : ''}</td>
            <td>${botonesAccion}</td>
        </tr>
    `;
                tbody.innerHTML += fila;
            });
        })
        .catch(error => {
            console.error("Error al cargar las donaciones:", error);
            tbody.innerHTML = `<tr><td colspan="7" class="text-center text-danger py-4">Error al conectar con el servidor.</td></tr>`;
        });
});