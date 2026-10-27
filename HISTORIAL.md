# HISTORIAL DE CAMBIOS

## [Fecha: 2026-10-27]

### 1. Inicialización de datos de Donaciones
- **Archivo modificado:** `src/main/java/pe/utp/proyectofinal/config/DataInitializer.java`
- **Cambios:**
  - Se agregó `DonacionRepository` e inicialización de 7 donaciones simuladas con sentido.
  - Métodos para crear donaciones con código de lote, donante, producto, cantidad y estado.

### 2. Corrección de carga de Solicitudes de Ayuda en vista
- **Archivos modificados:**
  - `src/main/resources/templates/solicitudes.html` - Corregido script a `JS/solicitud.js`
  - `src/main/java/pe/utp/proyectofinal/config/DataInitializer.java` - Agregados 5 solicitudes de ayuda simuladas
- **Razón:** El archivo JS correcto era `solicitud.js`, no `solicitudes.js`. También se agregaron datos iniciales.

### 3. Restricción del menú "Gestión" solo para administradores
- **Archivos modificados:**
  - `src/main/java/pe/utp/proyectofinal/controller/CampanaController.java` - Validación correcta de `ROLE_ADMIN`
  - `src/main/java/pe/utp/proyectofinal/controller/VistaController.java` - Protección de vistas `/solicitudes`, `/donaciones`, `/reportes` solo para ADMIN; agregado método `esAdmin()`
  - Plantillas: `campanas.html`, `solicitudes.html`, `donaciones.html`, `reportes.html`, `index.html`, `about.html`, `login.html`, `registro.html` - Dropdown "Gestión" oculto para no administradores/usuarios no autenticados
- **Razón:** Solo usuarios con rol ADMIN deben ver y acceder a Solicitudes, Recepción de Donaciones y Reportes.

### 4. Estilo del botón "Cerrar Sesión"
- **Archivos modificados:** Todas las plantillas con navbar
- **Cambio:** Botón rojo (`btn-danger`) con texto blanco, sin borde outline
- **Razón:** Mejor visibilidad y estilo solicitado.
