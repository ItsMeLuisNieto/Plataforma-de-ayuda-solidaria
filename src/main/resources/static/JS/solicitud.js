// Función para consumir el endpoint que creamos en Spring Boot
        fetch('/api/solicitudes')
            .then(response => response.json())
            .then(data => {
                const tbody = document.getElementById('tabla-solicitudes');
                tbody.innerHTML = ''; // Limpiar la tabla antes de cargar
                
                data.forEach(solicitud => {
                    const fila = `<tr>
                        <td>${solicitud.id}</td>
                        <td>${solicitud.nombreDonantes}</td>
                        <td>${solicitud.tipoAyuda}</td>
                        <td>${solicitud.descripcion}</td>
                        <td><span class="badge bg-success">${solicitud.estado}</span></td>
                        <td>${solicitud.fechaRegistro}</td>
                    </tr>`;
                    tbody.innerHTML += fila;
                });
            })
            .catch(error => console.error('Error al cargar las solicitudes:', error));