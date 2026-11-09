# Manual para usar GET, POST, PUT y DELETE (Campaña, Donación, Testimonio)

## Base URL
Por defecto en entorno local: `http://localhost:8090`

## Autenticación / Seguridad

- **Login web**: `POST /login` (form) con `email` y `pass`. Usa Spring Security form login.
- **API REST**: para los endpoints que requieren autenticación usa **HTTP Basic Auth** (header `Authorization: Basic <base64(email:contraseña)>`). Thunder Client / Postman lo configuran en la pestaña Auth.

### Usuarios de prueba (creados automáticamente por `DataInitializer`)

| Email | Contraseña | Rol |
|---|---|---|
| `admin@kallpa.local` | `kallpa123` | ADMIN |
| `usuario@kallpa.local` | `kallpa123` | USUARIO |

### Matriz de permisos actual (SecurityConfig)

| Operación | Anónimo | USUARIO (logueado) | ADMIN |
|---|---|---|---|
| `GET /api/**` (todos los GET de la API) | ✅ 200 | ✅ 200 | ✅ 200 |
| `POST /api/testimonios` | ❌ 401 | ✅ 201 (se asigna a sí mismo) | ✅ 201 |
| `PUT /api/testimonios/{id}` | ❌ 401 | ✅ 200 (solo si es el dueño) | ✅ 200 |
| `DELETE /api/testimonios/{id}` | ❌ 401 | ❌ 403 | ✅ 204 |
| `POST/PUT/DELETE /api/**` (resto: campañas, donaciones, recepciones) | ❌ 401 | ❌ 403 | ✅ |
| `POST /campanas/**` (web MVC, crear/eliminar campañas) | ❌ redirect login | ❌ 403 | ✅ |

- **CSRF**: desactivado para `/api/**` (no necesitas token para probar la API con Thunder Client/Postman). En el sitio web (formularios Thymeleaf) sigue activo y el JS lo envía vía meta tags `_csrf`.
- **Errores en JSON**: las rutas `/api/**` responden `401` y `403` con body JSON `{"mensaje": "..."}` en lugar de redirigir a la vista de login:
  - Sin sesión: `401` → `{"mensaje": "Debes iniciar sesión para realizar esta acción"}`
  - Sin permisos suficientes: `403` → `{"mensaje": "No tienes permisos para realizar esta acción"}`

---

## 1. CAMPANA (REST API)

Ahora las campañas cuentan con **API REST JSON**. También se mantiene la vista MVC en `/campanas` para el sitio web.

**Base:** `/api/campanas`

### GET - Listar todas las campañas
```http
GET http://localhost:8090/api/campanas
```
- **Auth**: Público
- **Respuesta**: `200 OK` - `List<Campana>`

**Ejemplo de respuesta:**
```json
[
  {
    "id": 1,
    "nombre": "Canasta de alimentos",
    "titulo": "Canasta de alimentos",
    "descripcion": "Aportamos kits básicos para familias vulnerables.",
    "metaDias": 7,
    "porcentaje": 12,
    "ubicacion": "San Martín de Porres",
    "beneficiarios": 240,
    "meta": 18000,
    "fechaCreacion": "2026-10-28T10:00:00",
    "campanaId": 1,
    "campanaTitulo": "Canasta de alimentos",
    "campanaNombre": "Canasta de alimentos"
  }
]
```

### GET - Obtener campaña por ID
```http
GET http://localhost:8090/api/campanas/{id}
```
- **Auth**: Público
- **Path param**: `id` (Long)
- **Respuesta**: `200 OK` - `Campana`
- **Error**: `404 Not Found` si no existe

**Ejemplo:**
```http
GET http://localhost:8090/api/campanas/1
```

### POST - Crear campaña
```http
POST http://localhost:8090/api/campanas
Content-Type: application/json
Authorization: Basic <admin@kallpa.local:kallpa123>
```
- **Auth**: **ADMIN** (Basic Auth). Sin sesión → `401`; con USUARIO → `403`
- **Body**: `CampanaRequest`

| Campo | Tipo | Requerido | Validación |
|---|---|---|---|
| nombre | String | Sí | @NotBlank |
| descripcion | String | Sí | @NotBlank |
| metaDias | Integer | Sí | @Min(1), @Max(365) |
| porcentaje | Integer | Sí | @Min(0), @Max(100) |
| ubicacion | String | Sí | @NotBlank |
| beneficiarios | Integer | Sí | @Min(1), @Max(1000000) |
| meta | Integer | Sí | @Min(0) |

**Ejemplo body:**
```json
{
  "nombre": "Abrigo para todos",
  "descripcion": "Recolectamos abrigos para personas en situación de calle",
  "metaDias": 15,
  "porcentaje": 0,
  "ubicacion": "Lima Centro",
  "beneficiarios": 150,
  "meta": 5000
}
```

**Respuesta**: `201 CREATED` - `Campana` creada

### PUT - Actualizar campaña
```http
PUT http://localhost:8090/api/campanas/{id}
Content-Type: application/json
Authorization: Basic <admin@kallpa.local:kallpa123>
```
- **Auth**: **ADMIN** (Basic Auth)
- **Path param**: `id` (Long)
- **Body**: `CampanaRequest` (todos los campos)
- **Respuesta**: `200 OK` - `Campana` actualizada

**Ejemplo:**
```http
PUT http://localhost:8090/api/campanas/1
Content-Type: application/json

{
  "nombre": "Canasta de alimentos actualizada",
  "descripcion": "Aportamos kits básicos mejorados",
  "metaDias": 10,
  "porcentaje": 25,
  "ubicacion": "San Martín de Porres",
  "beneficiarios": 300,
  "meta": 20000
}
```

### DELETE - Eliminar campaña
```http
DELETE http://localhost:8090/api/campanas/{id}
Authorization: Basic <admin@kallpa.local:kallpa123>
```
- **Auth**: **ADMIN** (Basic Auth)
- **Path param**: `id` (Long)
- **Respuesta**: `204 NO_CONTENT`

**Ejemplo:**
```http
DELETE http://localhost:8090/api/campanas/5
```

> **Nota:** También existe la vista MVC en `GET /campanas` (HTML) y `POST /campanas` / `POST /campanas/eliminar/{id}` para el sitio web (ambos POST requieren ADMIN). Usa `/api/campanas` para consumir JSON.

---

## 2. DONACIÓN (REST API)

Base: `/api/donaciones` - `DonacionController` (@RestController)

### GET - Listar todas las donaciones
```http
GET http://localhost:8090/api/donaciones
```
- **Auth**: Público
- **Respuesta**: `200 OK` - `List<Donacion>`

**Ejemplo de respuesta:**
```json
[
  {
    "id": 1,
    "codigoLote": "LOTE-1001",
    "nombreDonante": "Supermercado MegaPerú",
    "producto": "Arroz",
    "cantidad": 150,
    "estado": "CONFIRMADO",
    "fechaIngreso": "2026-10-28T10:30:00",
    "campana": {
      "id": 1,
      "nombre": "Canasta de alimentos",
      "titulo": "Canasta de alimentos"
    },
    "campanaId": 1,
    "campanaTitulo": "Canasta de alimentos",
    "campanaNombre": "Canasta de alimentos"
  }
]
```

### GET - Obtener donación por ID
```http
GET http://localhost:8090/api/donaciones/{id}
```
- **Auth**: Público
- **Path param**: `id` (Long)
- **Respuesta**: `200 OK` - `Donacion`
- **Error**: `404 Not Found` si no existe

**Ejemplo:**
```http
GET http://localhost:8090/api/donaciones/1
```

### POST - Registrar donación
```http
POST http://localhost:8090/api/donaciones
Content-Type: application/json
Authorization: Basic <admin@kallpa.local:kallpa123>
```
- **Auth**: **ADMIN** (Basic Auth). Sin sesión → `401`; con USUARIO → `403`
- **Body**: `DonacionRequest`

| Campo | Tipo | Requerido | Validación |
|---|---|---|---|
| campanaId | Long | Sí | @NotNull |
| nombreDonante | String | Sí | @NotBlank |
| producto | String | Sí | @NotBlank |
| cantidad | Integer | Sí | @NotNull, @Min(1) |
| codigoLote | String | No | Opcional (se genera automáticamente si no se envía) |
| estado | String | No | Opcional (si no se envía queda en `PENDIENTE`) |

**Ejemplo body:**
```json
{
  "campanaId": 1,
  "nombreDonante": "Juan Pérez",
  "producto": "Arroz",
  "cantidad": 10
}
```

**Respuesta**: `201 CREATED`
```json
{
  "exito": true,
  "mensaje": "Donación y lote registrados correctamente",
  "data": {
    "id": 15,
    "codigoLote": "LOTE-1730123456789",
    "nombreDonante": "Juan Pérez",
    "producto": "Arroz",
    "cantidad": 10,
    "estado": "PENDIENTE",
    "fechaIngreso": "2026-10-28T14:22:33",
    "campanaId": 1
  }
}
```

**Notas importantes:**
- Al registrar, automáticamente se crea un registro en `recepcion_donaciones` con estado `PENDIENTE` (vinculado a donación y campaña)
- Si no se envía estado, queda en `PENDIENTE`
- Si no se envía codigoLote, se genera `LOTE-<timestamp>`
- El campo `estado` por defecto en BD ahora es `PENDIENTE`

### PUT - Actualizar donación
```http
PUT http://localhost:8090/api/donaciones/{id}
Content-Type: application/json
Authorization: Basic <admin@kallpa.local:kallpa123>
```
- **Auth**: **ADMIN** (Basic Auth)
- **Path param**: `id` (Long)
- **Body**: `DonacionRequest`
- **Respuesta**: `200 OK` - `Donacion` actualizada

**Ejemplo:**
```http
PUT http://localhost:8090/api/donaciones/15
Content-Type: application/json

{
  "campanaId": 1,
  "nombreDonante": "Juan Pérez García",
  "producto": "Arroz Extra",
  "cantidad": 20,
  "codigoLote": "LOTE-1730123456789",
  "estado": "CONFIRMADO"
}
```

### DELETE - Eliminar donación
```http
DELETE http://localhost:8090/api/donaciones/{id}
Authorization: Basic <admin@kallpa.local:kallpa123>
```
- **Auth**: **ADMIN** (Basic Auth)
- **Path param**: `id` (Long)
- **Respuesta**: `204 NO_CONTENT`

**Ejemplo:**
```http
DELETE http://localhost:8090/api/donaciones/15
```

---

## 3. TESTIMONIO (REST API)

Base: `/api/testimonios` - `TestimoniosController` (@RestController)  
**CRUD COMPLETO DISPONIBLE** · Regla de propiedad: cada usuario solo puede crear/editar **sus propios** testimonios; el ADMIN puede hacer todo.

### GET - Listar todos los testimonios
```http
GET http://localhost:8090/api/testimonios
```
- **Auth**: Público
- **Respuesta**: `200 OK` - `List<Testimonio>`

**Ejemplo de respuesta:**
```json
[
  {
    "id": 1,
    "nombre": "Luna Torres",
    "rol": "Donante",
    "comentario": "Increíble la transparencia con la que entregan la ayuda humanitaria en cada campaña.",
    "fechaCreacion": "2026-10-28T10:05:00"
  }
]
```

### GET - Obtener testimonio por ID
```http
GET http://localhost:8090/api/testimonios/{id}
```
- **Auth**: Público
- **Path param**: `id` (Long)
- **Respuesta**: `200 OK` - `Testimonio`
- **Error**: `404 Not Found` si no existe

**Ejemplo:**
```http
GET http://localhost:8090/api/testimonios/1
```

### POST - Crear testimonio
```http
POST http://localhost:8090/api/testimonios
Content-Type: application/json
Authorization: Basic <usuario o admin>
```
- **Auth**: Autenticado (USUARIO o ADMIN). Sin sesión → `401`
- **Body**: `TestimonioRequest`

| Campo | Tipo | Requerido | Notas |
|---|---|---|---|
| comentario | String | Sí | Contenido del testimonio |
| usuarioId | Long | No | **Solo lo usa el ADMIN** para publicar en nombre de otro usuario. Un USUARIO normal lo ignora (el testimonio se le asigna automáticamente) |

**Ejemplo body (usuario normal):**
```json
{
  "comentario": "Excelente labor solidaria. Me alegra formar parte de este proyecto."
}
```

**Ejemplo body (admin publicando en nombre del usuario 2):**
```json
{
  "comentario": "Excelente labor solidaria.",
  "usuarioId": 2
}
```

**Respuesta**: `201 CREATED` - `Testimonio` creado

**Reglas de propiedad:**
- Un USUARIO autenticado crea el testimonio **a su nombre** siempre; si envía `usuarioId` de otra persona, se ignora (no puede suplantar a nadie)
- Solo el ADMIN puede usar `usuarioId` para asignar otro propietario

### PUT - Actualizar testimonio
```http
PUT http://localhost:8090/api/testimonios/{id}
Content-Type: application/json
Authorization: Basic <usuario duenio o admin>
```
- **Auth**: Autenticado. USUARIO solo puede editar **sus propios** testimonios; ADMIN puede editar cualquiera
- **Path param**: `id` (Long)
- **Body**: `TestimonioRequest` (comentario; `usuarioId` solo ADMIN para reasignar)
- **Respuesta**: `200 OK` - `Testimonio` actualizado
- **Error**: `403` con `{"mensaje": "Solo puedes editar tus propios testimonios"}` si un USUARIO edita un testimonio ajeno

**Ejemplo:**
```http
PUT http://localhost:8090/api/testimonios/1
Content-Type: application/json

{
  "comentario": "Sigue siendo una experiencia increíble, 100% recomendada."
}
```

### DELETE - Eliminar testimonio
```http
DELETE http://localhost:8090/api/testimonios/{id}
Authorization: Basic <admin@kallpa.local:kallpa123>
```
- **Auth**: **ADMIN** (Basic Auth). Con USUARIO → `403`
- **Path param**: `id` (Long)
- **Respuesta**: `204 NO_CONTENT`

**Ejemplo:**
```http
DELETE http://localhost:8090/api/testimonios/1
```

---

## Endpoints relacionados con recepción de donaciones (adicionales)

Dado que el flujo ahora va por recepción, puedes usar estos endpoints para gestionar la aprobación/rechazo. **Confirmar y rechazar requieren rol ADMIN** (Basic Auth):

```http
GET http://localhost:8090/api/recepcion-donaciones          # Público
GET http://localhost:8090/api/recepcion-donaciones/pendientes   # Público
POST http://localhost:8090/api/recepcion-donaciones/{id}/confirmar  # ADMIN
POST http://localhost:8090/api/recepcion-donaciones/{id}/rechazar   # ADMIN, body opcional {"motivo": "..."}
```

**Ejemplo rechazar:**
```http
POST http://localhost:8090/api/recepcion-donaciones/5/rechazar
Content-Type: application/json
Authorization: Basic <admin@kallpa.local:kallpa123>

{
  "motivo": "Producto en mal estado"
}
```

---

## Notas importantes

- **MVC vs REST**: `GET /campanas` sigue devolviendo HTML (vista Thymeleaf). Para obtener solo JSON usa `GET /api/campanas`.
- **GET por ID**: Disponible para `/api/campanas/{id}`, `/api/donaciones/{id}` y `/api/testimonios/{id}`.
- **CRUD completo**: Campañas, Donaciones y Testimonios cuentan con GET (lista + por id), POST, PUT y DELETE vía REST API.
- **Estado de donaciones**: Al crear via API, llegan con estado `PENDIENTE` y se registran automáticamente en `recepcion_donaciones`. Los reportes solo muestran donaciones con estado `CONFIRMADO`.
- **CSRF**: está desactivado para todas las rutas `/api/**`, así que puedes probar con Thunder Client/Postman usando solo Basic Auth, sin token. Solo el sitio web (formularios Thymeleaf/fetch de vistas) usa token CSRF vía meta tags `_csrf`.
- **Seguridad**: usuarios normales solo tienen lectura (GET) y gestión de sus propios testimonios; todo lo demás (crear/editar/eliminar campañas, donaciones, recepciones, testimonios ajenos) requiere rol ADMIN.
