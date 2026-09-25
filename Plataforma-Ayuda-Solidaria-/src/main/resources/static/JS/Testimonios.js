console.log("Probando 1 2 3 :D")

const API_URL = "http://localhost:8081/api/testimonios";

// Variable para guardar la lista en memoria 
let listaGlobalTestimonios = [];
document.addEventListener("DOMContentLoaded", () => {
    cargarTestimonios();
});

async function cargarTestimonios() {
    try {
        console.log("Intentando conectar con:", API_URL);
        const respuesta = await fetch(API_URL)

        if (!respuesta.ok) throw new Error("Error al consultar API")
        console.log("Estado de la respuesta HTTP:", respuesta.status);
        const testimonios = await respuesta.json();
        console.log("Datos recibidos de Spring Boot:", testimonios);
        listaGlobalTestimonios = testimonios; // Guardar en memoria
        renderizarCarrusel(testimonios);

    } catch (error) {
        console.error("Error al cargar testimonios: ", error)
        document.getElementById("contenedor-testimonios").innerHTML = `
            <div class="carousel-item active p-4">
                <p class="text-danger">No se pudieron cargar los testimonios :(</p>
            </div>
        `
    }

}

//generando items  del carrusel 

function renderizarCarrusel(lista) {
    // Limpiar el contenedor antes de renderizar
    const contenedor = document.getElementById("contenedor-testimonios")
    if (!contenedor) return
    //esta linea elimina el spinner cargando testimonios, una vez que se cargan los testimonios, se elimina el spinner
    contenedor.innerHTML = ""

    //si en caso no hay ningun testimonio, se muestra un mensaje
    if (lista.length === 0) {
        contenedor.innerHTML = `
            <div class="carousel-item active p-4">
                <p class="text-muted">Sé el primero en compartir tu testimonio.</p>
            </div>
        `
        return
    }

    lista.forEach((item, index) => {
        const itemDiv = document.createElement("div");
        itemDiv.className = `carousel-item ${index === 0 ? 'active' : ''}`;

        itemDiv.innerHTML = `
            <div class="p-4 text-center">
                <p class="fs-5 fst-italic text-secondary">"${item.comentario}"</p>
                <h5 class="fw-bold mb-0 text-dark">${item.nombre}</h5>
                <small class="text-primary fw-semibold d-block mb-3">${item.rol}</small>
                
                <!-- UN SOLO BOTÓN ABAJO -->
                <button type="button" class="btn btn-outline-warning btn-sm rounded-pill px-3" onclick="abrirModalEditar(${item.id})">
                    <i class="bi bi-pencil me-1"></i> Editar
                </button>
            </div>
        `;

        contenedor.appendChild(itemDiv);
    });
}
//abrir modal junto con los datos existentes del testimonio
function abrirModalEditar(id) {
    const testimonio = listaGlobalTestimonios.find(t => t.id === id);
    if (!testimonio) {
        console.error("No se encontró el testimonio con ID:", id);
        return;
    }

    // Inyecta los valores actuales en los inputs del modal
    document.getElementById("edit-id").value = testimonio.id;
    document.getElementById("edit-nombre").value = testimonio.nombre;
    document.getElementById("edit-rol").value = testimonio.rol;
    document.getElementById("edit-comentario").value = testimonio.comentario;

    // Muestra el modal con Bootstrap
    const modalEl = document.getElementById("modalEditarTestimonio");
    const modalBS = bootstrap.Modal.getOrCreateInstance(modalEl);
    modalBS.show();
}

//Envía la actualización a Spring Boot mediante PUT
async function guardarEdicionTestimonio(event) {
    if (event) event.preventDefault();

    const form = document.getElementById("formEditarTestimonio");
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }

    const id = document.getElementById("edit-id").value;
    const datosActualizados = {
        nombre: document.getElementById("edit-nombre").value.trim(),
        rol: document.getElementById("edit-rol").value,
        comentario: document.getElementById("edit-comentario").value.trim()
    };

    try {
        const respuesta = await fetch(`${API_URL}/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(datosActualizados)
        });

        if (respuesta.ok) {
            // Cerrar el modal de edición
            const modalEl = document.getElementById("modalEditarTestimonio");
            const modalBS = bootstrap.Modal.getInstance(modalEl);
            if (modalBS) modalBS.hide();

            // Recargar testimonios en vivo
            await cargarTestimonios();
        } else {
            alert("No se pudo actualizar el testimonio.");
        }
    } catch (error) {
        console.error("Error al actualizar el testimonio:", error);
        alert("Ocurrió un error al conectar con el servidor.");
    }
}


async function guardarTestimonio(event) {
    if (event) event.preventDefault();

    const form = document.getElementById("formTestimonio");


    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }

    const nombreInput = document.getElementById("nombre").value.trim();
    const rolInput = document.getElementById("rol").value;
    const comentarioInput = document.getElementById("comentario").value.trim();

    const nuevoTestimonio = {
        nombre: nombreInput,
        rol: rolInput,
        comentario: comentarioInput
    };

    try {
        const respuesta = await fetch(API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(nuevoTestimonio)
        });

        if (respuesta.ok) {
            // Limpiar los inputs
            form.reset();

            // Cerrar el modal
            const modalEl = document.getElementById("modalTestimonio");
            const modalBS = bootstrap.Modal.getInstance(modalEl);
            if (modalBS) modalBS.hide();

            // Recargar el carrusel
            await cargarTestimonios();
        } else {
            alert("No se pudo guardar el testimonio. Inténtalo de nuevo.");
        }
    } catch (error) {
        console.error("Error al enviar el testimonio:", error);
        alert("Ocurrió un error al conectar con el servidor.");
    }
}
