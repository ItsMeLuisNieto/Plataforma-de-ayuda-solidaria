# Manual para usar GET, POST, PUT y DELETE (Campaña, Donación, Testimonio)

## Base URL
Por defecto en entorno local: `http://localhost:8090`

## Autenticación / Seguridad
- **Login**: `POST /login` (form) con `email` y `pass`. Usa Spring Security form login.
- **CSRF**: Activado por defecto. Si llamas desde JS/cliente con formularios Thymeleaf, usa los meta tags `_csrf`. Si haces fetch desde JS, envía header `X-CSRF-TOKEN`.
- **Restricciones importantes**:
  - `POST /api/donaciones` requiere estar autenticado
  - `PUT /api/donaciones/{id}`, `DELETE /api/donaciones/{id}` requieren estar autenticado (según configuración actual)
  - `POST /api/recepcion-donaciones/**` requiere estar autenticado
  - `POST /campanas/**` requiere estar autenticado (cualquier usuario autenticado puede crear/eliminar campañas según config actual)
  - `POST /api/campanas`, `PUT /api/campanas/**`, `DELETE /api/campanas/**` - Se recomienda requerir autenticación/rol ADMIN
  - `GET /api/donaciones`, `GET /api/campanas`, `GET /api/testimonios` son públicos (permitAll)
  - Endpoints de Testimonio (`POST/PUT/DELETE /api/testimonios/**`) están **públicos** actualmente (sin restricción de auth). Se recomienda restringir a ADMIN.

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
```
- **Auth**: Requiere autenticación (recomendado ADMIN)
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

**Respuesta**: `200 OK` - `Campana` creada

### PUT - Actualizar campaña
```http
PUT http://localhost:8090/api/campanas/{id}
Content-Type: application/json
```
- **Auth**: Requiere autenticación (recomendado ADMIN)
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
```
- **Auth**: Requiere autenticación (recomendado ADMIN)
- **Path param**: `id` (Long)
- **Respuesta**: `200 OK` (void)

**Ejemplo:**
```http
DELETE http://localhost:8090/api/campanas/5
```

> **Nota:** También existe la vista MVC en `GET /campanas` (HTML) y `POST /campanas` / `POST /campanas/eliminar/{id}` para el sitio web. Usa `/api/campanas` para consumir JSON.

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
```
- **Auth**: Requiere autenticación
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
```
- **Auth**: Requiere autenticación (recomendado ADMIN)
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
```
- **Auth**: Requiere autenticación (recomendado ADMIN)
- **Path param**: `id` (Long)
- **Respuesta**: `204 NO_CONTENT`

**Ejemplo:**
```http
DELETE http://localhost:8090/api/donaciones/15
```

---

## 3. TESTIMONIO (REST API)

Base: `/api/testimonios` - `TestimoniosController` (@RestController)  
**CRUD COMPLETO DISPONIBLE**

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
```
- **Auth**: **Público** (sin restricción en SecurityConfig). Se recomienda restringir a usuarios autenticados o ADMIN
- **Body**: `TestimonioRequest`

| Campo | Tipo | Requerido | Validación |
|---|---|---|---|
| nombre | String | Sí | @NotBlank |
| rol | String | Sí | @NotBlank |
| comentario | String | Sí | @NotBlank |

**Ejemplo body:**
```json
{
  "nombre": "María López",
  "rol": "Voluntaria",
  "comentario": "Excelente labor solidaria. Me alegra formar parte de este proyecto."
}
```

**Respuesta**: `201 CREATED` - `Testimonio` creado

### PUT - Actualizar testimonio
```http
PUT http://localhost:8090/api/testimonios/{id}
Content-Type: application/json
```
- **Auth**: **Público** (sin restricción en SecurityConfig). Se recomienda restringir a ADMIN
- **Path param**: `id` (Long)
- **Body**: `TestimonioRequest` (todos los campos requeridos)
- **Respuesta**: `200 OK` - `Testimonio` actualizado

**Ejemplo:**
```http
PUT http://localhost:8090/api/testimonios/1
Content-Type: application/json

{
  "nombre": "María López",
  "rol": "Voluntaria",
  "comentario": "Sigue siendo una experiencia increíble, 100% recomendada."
}
```

### DELETE - Eliminar testimonio
```http
DELETE http://localhost:8090/api/testimonios/{id}
```
- **Auth**: **Público** (sin restricción en SecurityConfig). Se recomienda restringir a ADMIN
- **Path param**: `id` (Long)
- **Respuesta**: `204 NO_CONTENT`

**Ejemplo:**
```http
DELETE http://localhost:8090/api/testimonios/1
```

---

## Endpoints relacionados con recepción de donaciones (adicionales)

Dado que el flujo ahora va por recepción, puedes usar estos endpoints para gestionar la aprobación/rechazo:

```http
GET http://localhost:8090/api/recepcion-donaciones          # Listar todos (con donación y campaña)
GET http://localhost:8090/api/recepcion-donaciones/pendientes
POST http://localhost:8090/api/recepcion-donaciones/{id}/confirmar  # Requiere auth
POST http://localhost:8090/api/recepcion-donaciones/{id}/rechazar   # Requiere auth, body opcional {"motivo": "..."}
```

**Ejemplo rechazar:**
```http
POST http://localhost:8090/api/recepcion-donaciones/5/rechazar
Content-Type: application/json

{
  "motivo": "Producto en mal estado"
}
```

---

## Notas importantes

- **MVC vs REST**: `GET /campanas` sigue devolviendo HTML (vista Thymeleaf). Para obtener solo JSON usa `GET /api/campanas`.
- **GET por ID**: Ahora disponible para `/api/campanas/{id}`, `/api/donaciones/{id}` y `/api/testimonios/{id}`.
- **CRUD completo**: Campañas, Donaciones y Testimonios cuentan ahora con GET (lista + por id), POST, PUT y DELETE vía REST API.
- **Estado de donaciones**: Al crear via API, llegan con estado `PENDIENTE` y se registran automáticamente en `recepcion_donaciones`. Los reportes solo muestran donaciones con estado `CONFIRMADO`.
- **CSRF en Postman**: Si pruebas con Postman fuera de sesión, puedes deshabilitarlo temporalmente o enviar el token CSRF. Para APIs REST puras suele usarse JWT o deshabilitar CSRF para ciertas rutas.
- **Seguridad**: Se recomienda restringir `POST/PUT/DELETE` a roles `ROLE_ADMIN` para mayor control.
