# Contexto del proyecto — Kallpa Solidary

> Archivo de contexto para retomar el trabajo sin perder decisiones, estado y pendientes.
> Última actualización: 2026-10-30.

## 1. Resumen

- **Proyecto:** Plataforma de ayuda solidaria “Kallpa Solidary”.
- **Tipo:** Aplicación web monolítica Spring Boot con vistas Thymeleaf y API REST.
- **Stack:** Java 21, Spring Boot 4.1.1, Spring Security, Spring Data JPA, Hibernate, Thymeleaf, PostgreSQL (Neon), Bootstrap, JavaScript vanilla.
- **Funcionalidad:** Campañas solidarias, donaciones, recepción/confirmación de donaciones, testimonios, reportes y exportación a Excel/PDF.
- **Estado actual:** El proyecto compila, la aplicación inicia y los endpoints principales responden `200`. La base de datos ya usa las columnas nuevas `id_campanas`, `id_usuarios`, etc.

## 2. Cómo ejecutar

```bash
# Desde la raíz del proyecto
./mvnw.cmd spring-boot:run

# Compilar
./mvnw.cmd -q -DskipTests compile

# Empaquetar
./mvnw.cmd -q -DskipTests package

# Ejecutar el JAR
java -jar target/proyectofinal-0.0.1-SNAPSHOT.jar
```

- **URL local:** `http://localhost:8090`
- **Puerto configurable:** variable `SERVER_PORT`.
- **Credenciales semilla:**
  - Admin: `admin@kallpa.local` / `kallpa123` → rol `ADMIN`
  - Usuario: `usuario@kallpa.local` / `kallpa123` → rol `USUARIO`
- Si el puerto `8090` está ocupado, probablemente ya hay una instancia corriendo desde VS Code/Java. Deténla antes de lanzar otra.

## 3. Configuración principal

Archivo: `src/main/resources/application.properties`

| Propiedad | Valor / variable | Nota |
|---|---|---|
| `server.port` | `${SERVER_PORT:8090}` | Puerto HTTP |
| `spring.datasource.url` | `${DB_URL:jdbc:postgresql://ep-raspy-field-b5rcv2mt-pooler.c-7.us-east-2.aws.neon.tech/neondb?sslmode=require}` | Neon PostgreSQL |
| `spring.datasource.username` | `${DB_USERNAME:neondb_owner}` | Usuario de BD |
| `spring.datasource.password` | `${DB_PASSWORD:...}` | Credencial de desarrollo embebida; no publicar |
| `spring.jpa.hibernate.ddl-auto` | `${JPA_DDL_AUTO:update}` | No usar `create` salvo para resetear esquema |
| `spring.jpa.open-in-view` | `false` | Obliga a cargar relaciones con `EntityGraph` |
| `spring.jackson.deserialization.fail-on-unknown-properties` | `false` | La API tolera campos extra |
| `app.google.client-id` | `${GOOGLE_CLIENT_ID:...}` | Login con Google |

- **Proyecto Neon:** `KallpaSolidaryPrueba`
- **Project ID:** `fragrant-mud-66138567`
- **Host:** `ep-raspy-field-b5rcv2mt-pooler.c-7.us-east-2.aws.neon.tech`
- **Base de datos:** `neondb`
- **Usuario BD:** `neondb_owner`

## 4. Base de datos

### Tablas y claves

| Tabla | PK | Columnas principales | FK |
|---|---|---|---|
| `usuarios` | `id_usuarios` | `email`, `nombre`, `password`, `rol` | — |
| `campanas` | `id_campanas` | `nombre`, `descripcion`, `meta_dias`, `porcentaje`, `ubicacion`, `beneficiarios`, `meta`, `fecha_creacion` | `usuario_id` → `usuarios.id_usuarios` |
| `testimonios` | `id_testimonios` | `comentario`, `fecha_creacion` | `usuario_id` → `usuarios.id_usuarios` |
| `donaciones` | `id_donaciones` | `codigo_lote`, `nombre_donante`, `producto`, `cantidad`, `estado`, `fecha_ingreso` | `campana_id` → `campanas.id_campanas` |
| `recepcion_donaciones` | `id_recepcion_donaciones` | `estado`, `motivo_rechazo`, `fecha_solicitud`, `fecha_revision`, `revisado_por` | `donacion_id` → `donaciones.id_donaciones`; `campana_id` → `campanas.id_campanas` |

- Las columnas antiguas `id` **ya no existen**. Fueron reemplazadas por las PK con nombre explícito.
- En Java los campos siguen llamándose `id` y el JSON sigue exponiendo `id`; solo cambió el nombre de la columna en BD.
- Las FK conservan nombres como `usuario_id`, `campana_id`, `donacion_id`.

### Datos semilla actuales

- 2 usuarios: `Administrador` (`ADMIN`) y `Nieto Paz` (`USUARIO`, según el código actual).
- 3 campañas.
- 2 testimonios.
- 7 donaciones.
- 7 recepciones de donación.

> Nota: la BD que se sembró en la última recreación conserva el nombre `Usuario Kallpa` para el usuario `usuario@kallpa.local`. El código actual de `DataInitializer` usa `Nieto Paz`, pero `obtenerOCrearUsuario` no actualiza usuarios existentes. Si se quiere alinear, hay que cambiar el nombre en BD o ajustar el seed.

### Historial importante del esquema

1. Se intentó renombrar las PK solo en entidades y se hizo `TRUNCATE`.
2. `TRUNCATE` no elimina columnas: quedaron las columnas viejas `id` y Hibernate agregó las nuevas `id_*`.
3. La FK `campanas.usuario_id` seguía apuntando a `usuarios.id`, pero Hibernate insertaba valores de `usuarios.id_usuarios`; eso produjo una violación de llave foránea al crear campañas.
4. Se recreó el esquema una sola vez con `JPA_DDL_AUTO=create`, sin SQL manual. Luego se volvió a `update`.
5. Verificado: las PK son `id_*`, las FK apuntan a las PK nuevas y los endpoints responden `200`.

## 5. Seguridad y roles

- Roles manejados: `ADMIN` y `USUARIO`.
- `UserDetailsServiceImpl` crea autoridades con `.roles(usuario.getRol())`, por lo que en Spring Security quedan como `ROLE_ADMIN` y `ROLE_USUARIO`.
- Registro público (`/registrar`) siempre asigna `USUARIO`.
- `DataInitializer` crea el admin y el usuario semilla.
- La UI usa `sec:authorize="hasRole('ADMIN')"` para mostrar:
  - El dropdown **Gestión** en la navbar.
  - El botón **Nueva Campaña**.
  - Los formularios de **eliminar campaña**.
- Los administradores ven esas opciones en todas las vistas donde existe la navbar.
- Los usuarios `USUARIO` **no ven** el dropdown Gestión ni el botón Nueva Campaña.

### Reglas actuales de `SecurityConfig`

- `/api/**`: `permitAll()` y CSRF ignorado (pensado para Thunder Client/Postman).
- `POST /campanas/**`: requiere autenticación, **no** requiere rol `ADMIN`.
- `/donar-ahora`, `/ver-voluntario`, `/donar-bienes`, `/eventos`, `/ver-campanas`: requieren autenticación.
- `/reportes` y `/donaciones` (vistas): el controlador redirige si no es `ADMIN`.
- Exportaciones `/reportes/exportar/excel` y `/reportes/exportar/pdf`: devuelven `403` si no es `ADMIN`.

> Decisión actual: se dejó que cualquier usuario autenticado pueda invocar `POST /campanas/**` para no complicar la autorización. La UI sigue ocultando las opciones a los no administradores.

## 6. Modelo de dominio

- **Usuario:** email único, nombre, password BCrypt, rol (`ADMIN`/`USUARIO`), relaciones `campanas` y `testimonios`.
- **Campana:** nombre, descripción, metaDias, porcentaje, ubicación, beneficiarios, meta, fechaCreacion, creador (`usuario`) y donaciones.
- **Testimonio:** usuario autor, comentario, fechaCreacion. Expone `nombre` y `rol` derivados del usuario.
- **Donacion:** código de lote único, donante, producto, cantidad, estado, fechaIngreso y campaña.
- **RecepcionDonacion:** donación, campaña, estado, motivoRechazo, fechas y `revisadoPor`.

### Relaciones y carga

- `Campana.usuario` y `Testimonio.usuario` son `LAZY`.
- `Donacion.campana` es `LAZY`.
- `RecepcionDonacion.donacion` y `RecepcionDonacion.campana` son `EAGER`.
- Como `open-in-view=false`, los repositorios usan `@EntityGraph` para cargar relaciones antes de serializar:
  - `CampanaRepository`: `usuario`.
  - `TestimonioRepository`: `usuario`.
  - `DonacionRepository`: `campana`, `campana.usuario`.
  - `RecepcionDonacionRepository`: `donacion`, `campana`, `campana.usuario`.

## 7. API REST

### Campañas — `/api/campanas`

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/campanas` | Lista campañas |
| `GET` | `/api/campanas/{id}` | Obtiene una campaña |
| `POST` | `/api/campanas` | Crea campaña |
| `PUT` | `/api/campanas/{id}` | Actualiza campaña |
| `DELETE` | `/api/campanas/{id}` | Elimina campaña y sus dependencias |

Payload de campaña (`CampanaRequest`):

```json
{
  "nombre": "Canasta de alimentos",
  "descripcion": "Apoyo para familias vulnerables",
  "metaDias": 30,
  "porcentaje": 0,
  "ubicacion": "Lima, Perú",
  "beneficiarios": 100,
  "meta": 1000
}
```

- Si no se envía `nombre`, se usa `"Campaña Solidaria"`.
- Si no hay usuario autenticado, `CampanaService` asigna el primer usuario existente.

### Donaciones — `/api/donaciones`

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/donaciones` | Lista donaciones |
| `GET` | `/api/donaciones/{id}` | Obtiene donación |
| `POST` | `/api/donaciones` | Registra donación y crea recepción automática |
| `PUT` | `/api/donaciones/{id}` | Actualiza donación |
| `DELETE` | `/api/donaciones/{id}` | Elimina donación y sus recepciones |

Payload (`DonacionRequest`):

```json
{
  "campanaId": 1,
  "codigoLote": "LOTE-1001",
  "nombreDonante": "Supermercado MegaPerú",
  "producto": "Arroz",
  "cantidad": 150,
  "estado": "PENDIENTE"
}
```

- `POST` responde `201` con `{ "exito": true, "mensaje": "...", "data": {...} }`.
- Si no se envía campaña, usa la primera campaña existente o crea una por defecto.

### Testimonios — `/api/testimonios`

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/testimonios` | Lista testimonios |
| `GET` | `/api/testimonios/{id}` | Obtiene testimonio |
| `POST` | `/api/testimonios` | Crea testimonio (`201`) |
| `PUT` | `/api/testimonios/{id}` | Actualiza comentario |
| `DELETE` | `/api/testimonios/{id}` | Elimina (`204`) |

Payload (`TestimonioRequest`):

```json
{
  "comentario": "Gracias por la ayuda",
  "usuarioId": 2
}
```

- `usuarioId` es opcional si hay sesión autenticada; si no hay usuario, usa el primero existente.
- El frontend envía `usuarioId` obtenido de `/api/usuario/actual`.

### Recepción de donaciones — `/api/recepcion-donaciones`

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/recepcion-donaciones` | Lista recepciones |
| `GET` | `/api/recepcion-donaciones/pendientes` | Lista pendientes |
| `POST` | `/api/recepcion-donaciones/{id}/confirmar` | Confirma recepción |
| `POST` | `/api/recepcion-donaciones/{id}/rechazar` | Rechaza recepción |

Payload para rechazar:

```json
{ "motivo": "Producto vencido" }
```

- `revisadoPor` se toma del usuario autenticado; si no hay sesión, se usa `"Sistema"`.

### Usuario actual — `/api/usuario/actual`

- `GET /api/usuario/actual`
- Devuelve el `Usuario` autenticado o `401` con `{ "error": "No autenticado" }`.

### Login con Google — `POST /login/google`

```json
{ "token": "<google-id-token>" }
```

- Si el usuario no existe: `{ "accion": "completar_registro", "email": "...", "nombre": "..." }`.
- Si existe: crea la sesión y responde `{ "accion": "login_exitoso" }`.

### Reportes — exportación

- `GET /reportes/exportar/excel` → `.xlsx` (solo `ADMIN`).
- `GET /reportes/exportar/pdf` → `.pdf` (solo `ADMIN`).

### Manejo de errores API

`ApiExceptionHandler` responde:

- `400` para validaciones y `BusinessException`.
- `404` para `NotFoundException`.
- `500` para errores no controlados.

## 8. Vistas y frontend

### Templates Thymeleaf

- `index.html`: página principal.
- `about.html`: “Sobre nosotros”.
- `campanas.html`: listado de campañas, modal de creación y eliminación.
- `donaciones.html`: gestión de recepción de donaciones (admin).
- `reportes.html`: reportes y exportaciones (admin).
- `login.html`: inicio de sesión.
- `registro.html`: registro de usuarios.

### JavaScript

- `static/JS/Testimonios.js`: carga, crea, edita y elimina testimonios.
  - Usa `GET /api/usuario/actual`.
  - `POST /api/testimonios` con `{ comentario, usuarioId }`.
  - `PUT /api/testimonios/{id}` con `{ comentario }`.
  - Si recibe `401`, redirige a `/login`.
- `static/JS/donaciones.js`: lógica de recepción de donaciones.
- `static/JS/index.js`: interacciones de la página principal.
- `static/JS/csrf.js`: manejo de token CSRF para formularios.

### Visibilidad de “Gestión” y “Nueva Campaña”

- Antes se usaba `th:if="${esAdmin}"`, pero `esAdmin` no se inyectaba en todas las vistas; por eso el dropdown no aparecía en la página principal.
- Ahora se usa `sec:authorize="hasRole('ADMIN')"` directamente en las plantillas.
- Resultado verificado:
  - Admin ve “Gestión” y “Nueva Campaña”.
  - Usuario `USUARIO` no ve “Gestión” ni el botón “Nueva Campaña”.
  - El modal de “Registrar Nueva Campaña” puede seguir en el DOM para no administradores, pero el botón que lo abre está oculto.

## 9. Cambios recientes

- Renombrado de PK en entidades: `id_campanas`, `id_usuarios`, `id_testimonios`, `id_donaciones`, `id_recepcion_donaciones`.
- `Campana` ahora usa `nombre` como columna real y mantiene `getTitulo()` como alias.
- `Testimonio` ahora se relaciona con `Usuario`; `nombre` y `rol` se derivan del usuario.
- Roles normalizados a `ADMIN`/`USUARIO`; registro asigna `USUARIO`.
- `DataInitializer` crea admin y usuario semilla, y asigna campañas/testimonios a usuarios reales.
- Se eliminaron “Luna Torres” y “Enrique Lee” como testimonios hardcodeados.
- Se agregó `@EntityGraph` para resolver errores `500` por `LazyInitializationException` con `open-in-view=false`.
- Se corrigió la violación de FK al recrear el esquema con las PK nuevas.
- Se cambió la visibilidad de “Gestión”/“Nueva Campaña” a `sec:authorize` para que los admins las vean en todas las vistas.
- `CampanaController` ya no inyecta `esAdmin`; la autorización visual quedó en las plantillas.
- Se verificó que los endpoints principales responden `200` y que la UI distingue admin vs usuario.
- Se verificó que un `USUARIO` autenticado puede crear una campaña vía `POST /campanas`; la campaña quedó asignada a su usuario y luego se eliminó la prueba.

## 10. Problemas conocidos y notas

- **Credenciales de BD embebidas:** `application.properties` contiene la contraseña de Neon como valor por defecto. Antes de publicar el repositorio, mover a variables de entorno y rotar la credencial.
- **Sin tests:** no hay archivos en `src/test`; las dependencias de test están en `pom.xml`, pero no hay pruebas implementadas.
- **`ddl-auto=create` es destructivo:** solo usarlo para recrear el esquema desde cero. El valor normal es `update`.
- **API abierta:** `/api/**` es `permitAll`; cualquiera puede invocar la API sin autenticarse. Es intencional para pruebas, pero no para producción.
- **POST de campañas:** cualquier usuario autenticado puede crear/eliminar campañas vía `/campanas/**`; la UI solo lo muestra al admin. Si se quiere endurecer, cambiar a `hasRole('ADMIN')`.
- **Modal de campaña:** el formulario/modal puede existir en el DOM para usuarios no admin; solo el botón está oculto.
- **Nombre semilla:** código actual usa `Nieto Paz`; BD actual conserva `Usuario Kallpa`.
- **App en ejecución:** puede haber una instancia corriendo en `8090` (por ejemplo, desde VS Code). Verificar antes de lanzar otra.

## 11. Próximos pasos sugeridos

1. Definir si los usuarios `USUARIO` también deben ver el botón “Nueva Campaña” o solo invocar la API.
2. Mover credenciales de Neon a variables de entorno y rotar la contraseña.
3. Agregar tests de integración para campañas, donaciones, recepciones y testimonios.
4. Revisar si `/api/**` debe seguir abierto o requerir autenticación.
5. Validar en navegador el flujo completo: login admin, crear campaña, crear donación, confirmar recepción y ver reportes.
6. Si se cambia el seed, actualizar el nombre del usuario semilla en BD o hacer que `DataInitializer` actualice datos existentes.

## 12. Archivos clave

### Configuración
- `src/main/resources/application.properties`
- `src/main/java/pe/utp/proyectofinal/config/SecurityConfig.java`
- `src/main/java/pe/utp/proyectofinal/config/DataInitializer.java`
- `src/main/java/pe/utp/proyectofinal/config/GlobalControllerAdvice.java`
- `src/main/java/pe/utp/proyectofinal/config/ApiExceptionHandler.java`

### Entidades
- `entity/Usuario.java`
- `entity/Campana.java`
- `entity/Testimonio.java`
- `entity/Donacion.java`
- `entity/RecepcionDonacion.java`

### Repositorios
- `repository/UsuarioRepository.java`
- `repository/CampanaRepository.java`
- `repository/TestimonioRepository.java`
- `repository/DonacionRepository.java`
- `repository/RecepcionDonacionRepository.java`

### Servicios
- `service/UsuarioService.java`
- `service/UserDetailsServiceImpl.java`
- `service/CampanaService.java`
- `service/TestimonioService.java`
- `service/DonacionService.java`
- `service/RecepcionDonacionService.java`
- `service/ReporteService.java`
- `service/GoogleAuthService.java`

### Controladores
- `controller/VistaController.java`
- `controller/CampanaController.java`
- `controller/CampanaApiController.java`
- `controller/TestimoniosController.java`
- `controller/DonacionController.java`
- `controller/RecepcionDonacionController.java`
- `controller/UserController.java`
- `controller/AuthController.java`
- `controller/GoogleAuthController.java`
- `controller/ReporteExportController.java`

### Frontend
- `templates/index.html`
- `templates/about.html`
- `templates/campanas.html`
- `templates/donaciones.html`
- `templates/reportes.html`
- `templates/login.html`
- `templates/registro.html`
- `static/JS/Testimonios.js`
- `static/JS/donaciones.js`
- `static/JS/index.js`
- `static/JS/csrf.js`

### DTOs
- `dto/CampanaRequest.java`
- `dto/DonacionRequest.java`
- `dto/TestimonioRequest.java`
- `dto/RegistroRequest.java`
- `dto/ReporteDTO.java`
- `dto/GoogleUserDTO.java`
