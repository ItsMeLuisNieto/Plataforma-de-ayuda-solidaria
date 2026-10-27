document.addEventListener("DOMContentLoaded", function () {
    const tbody = document.getElementById("tabla-donaciones");

    fetch('/api/donaciones')
        .then(response => response.json())
        .then(data => {
            tbody.innerHTML = ""; // Limpiar tabla
            
            if (!data || data.length === 0) {
                tbody.innerHTML = <tr><td colspan="7" class="text-center text-muted py-4">No hay donaciones registradas todavía.</td></tr>;
                return;
            }

            data.forEach(donacion => {
                const fila = `
                    <tr>
                        <td class="fw-bold">${donacion.id}</td>
                        <td><span class="badge bg-secondary">${donacion.codigoLote}</span></td>
                        <td>${donacion.nombreDonante}</td>
                        <td>${donacion.producto}</td>
                        <td><span class="fw-semibold text-dark">${donacion.cantidad}</span></td>
                        <td><span class="badge bg-success">${donacion.estado}</span></td>
                        <td>${donacion.fechaIngreso ? donacion.fechaIngreso.replace('T', ' ').substring(0, 19) : ''}</td>
                    </tr>
                `;
                tbody.innerHTML += fila;
            });
        })
        .catch(error => {
            console.error("Error al cargar las donaciones:", error);
            tbody.innerHTML = <tr><td colspan="7" class="text-center text-danger py-4">Error al conectar con el servidor.</td></tr>;
        });
});