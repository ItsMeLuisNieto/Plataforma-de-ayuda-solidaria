# Historial del proyecto - Plataforma de Ayuda Solidaria (Kallpa Solidary)

> **REGLA OBLIGATORIA DE MANTENIMIENTO**
>
> Cada vez que se modifique cualquier archivo del proyecto, se debe actualizar este documento en el mismo cambio/commit. Como mínimo:
>
> 1. Actualizar la fecha de "Ultima actualizacion" y el estado de Git.
> 2. Agregar una entrada en "Registro de cambios" con lo que se hizo y por que.
> 3. Actualizar las secciones afectadas: rutas, entidades, DTOs, seguridad, tests, estructura, pendientes.
> 4. Si se agrega, elimina o renombra un archivo importante, reflejarlo.
> 5. No escribir contrasenas, tokens ni secretos reales en este archivo.
> 6. Si el cambio no esta terminado, dejarlo anotado en "Pendientes" o "Trabajo en curso".

Este archivo es la memoria del proyecto. Antes de trabajar en el repositorio, leerlo junto con `ARQUITECTURA.md`.

- **Ultima actualizacion:** 2026-10-25
- **Rama de trabajo:** `refactor/arquitectura-por-capas`
- **Commit actual:** `5b55b2b` - `refactor: arquitectura por capas, entidades y tests`
- **Rama `main` local y remota:** `8ea59e7` (sin modificar)
- **Estado del arbol de trabajo:** limpio, salvo este archivo si aun no se ha commiteado.

---

## 1. Resumen del proyecto

Aplicacion web Spring Boot para gestionar una plataforma de ayuda solidaria: campanas, donaciones, solicitudes de ayuda, testimonios, reportes y autenticacion de usuarios.

- **Nombre del artefacto:** `proyectofinal`
- **Paquete base:** `pe.utp.proyectofinal`
- **Puerto por defecto:** `8090`
- **Repositorio remoto:** `https://github.com/ItsMeLuisNieto/Plataforma-de-ayuda-solidaria.git`
- **Rama remota nueva:** `origin/refactor/arquitectura-por-capas`

## 2. Stack tecnologico

| Componente | Version / detalle |
|---|---|
| Java | 21 |
| Spring Boot | 4.1.1 |
| Build | Maven (`./mvnw`) |
| Persistencia | Spring Data JPA + Hibernate 7 |
| Base de datos productiva | PostgreSQL en Neon |
| Base de datos de tests | H2 en memoria |
| Vistas | Thymeleaf + Bootstrap |
| Seguridad | Spring Security, form login y Google OAuth2 |
| Validacion | Jakarta Bean Validation |
| Tests | JUnit 5, Mockito, Spring Boot Test, H2 |

## 3. Estado de Git

- Se trabajo primero en `main`, luego se creo la rama `refactor/arquitectura-por-capas` desde `main`.
- El commit del refactor esta solo en la rama nueva: `5b55b2b`.
- La rama nueva ya fue subida a GitHub con tracking `origin/refactor/arquitectura-por-capas`.
- `main` no fue modificada ni se hizo merge ni push a `main`.
- No se creo Pull Request todavia.
- La contrasena de Neon se mantuvo sin cambios, tal como pidio el usuario. No se debe reproducir su valor en documentacion.

## 4. Estructura actual

```text
src/main/java/pe/utp/proyectofinal/
├── ProyectofinalApplication.java
├── config/
│   ├── ApiExceptionHandler.java
│   ├── DataInitializer.java
│   ├── GlobalControllerAdvice.java
│   └── SecurityConfig.java
├── controller/
│   ├── AuthController.java
│   ├── CampanaController.java
│   ├── DonacionController.java
│   ├── GoogleAuthController.java
│   ├── SolicitudAyudaController.java
│   ├── TestimoniosController.java
│   ├── UserController.java
│   └── VistaController.java
├── dto/
│   ├── CampanaRequest.java
│   ├── DonacionRequest.java
│   ├── GoogleUserDTO.java
│   ├── RegistroRequest.java
│   ├── ReporteDTO.java
│   ├── SolicitudAyudaRequest.java
│   └── TestimonioRequest.java
├── entity/
│   ├── Campana.java
│   ├── Donacion.java
│   ├── SolicitudAyuda.java
│   ├── Testimonio.java
│   └── Usuario.java
├── exception/
│   ├── BusinessException.java
│   └── NotFoundException.java
├── repository/
│   ├── CampanaRepository.java
│   ├── DonacionRepository.java
│   ├── SolicitudAyudaRepository.java
│   ├── TestimonioRepository.java
│   └── UsuarioRepository.java
└── service/
    ├── CampanaService.java
    ├── DonacionService.java
    ├── GoogleAuthService.java
    ├── ReporteService.java
    ├── SolicitudAyudaService.java
    ├── TestimonioService.java
    ├── UserDetailsServiceImpl.java
    └── UsuarioService.java
```

Plantillas: `about.html`, `donaciones.html`, `donate.html`, `index.html`, `login.html`, `registro.html`, `reportes.html`, `solicitudes.html`.

Tests: `ProyectofinalApplicationTests` y tres tests de servicios (`CampanaServiceTest`, `DonacionServiceTest`, `UsuarioServiceTest`).

## 5. Arquitectura y decisiones

Flujo general:

```text
Controller -> Service -> Repository -> Entity -> PostgreSQL
```

Decisiones importantes:

1. **Controllers no acceden directamente a repositorios.** Siempre pasan por un Service.
2. **No existen DTOs `Response`.** Los services devuelven entidades y los controllers REST serializan la entidad como JSON. Se eliminaron `CampanaResponse`, `DonacionResponse`, `SolicitudAyudaResponse`, `TestimonioResponse` y `UsuarioResponse`.
3. **Los DTOs `Request` se mantienen** para validar entradas de formularios y de la API.
4. **`Usuario.password` tiene `@JsonIgnore`** para no exponer el hash en respuestas JSON.
5. **Las rutas y URLs existentes se conservaron** durante el refactor.
6. **La tabla heredada `"donciones"` fue reemplazada por `"donaciones"`.** Los datos antiguos no se migran automaticamente.
7. **`Campana` y `Testimonio` ahora son entidades JPA persistidas**, no listas en memoria.
8. **CSRF esta habilitado.** Las plantillas incluyen las meta etiquetas `_csrf` y `_csrf_header`; `csrf.js` existe para peticiones fetch.
9. **Los datos iniciales se siembran solo si las tablas estan vacias**: 3 campanas y 2 testimonios.

## 6. Rutas actuales

### Vistas MVC

| Metodo | Ruta | Acceso | Respuesta |
|---|---|---|---|
| GET | `/kallpa-solidary` | Publico | `index` |
| GET | `/sobre-nosotros` | Publico | `about` |
| GET | `/reportes` | Publico | `reportes` con KPIs y movimientos |
| GET | `/login` | Publico | `login` |
| GET | `/registro` | Publico | `registro` |
| GET | `/solicitudes` | Publico | `solicitudes` |
| GET | `/donaciones` | Publico | `donaciones` |
| GET | `/donar-ahora`, `/ver-voluntario`, `/donar-bienes` | Autenticado | Redirect a `/campanas` |
| GET | `/eventos` | Autenticado | Redirect a `/campanas#events` |
| GET | `/ver-campanas` | Autenticado | Redirect a `/campanas#campaigns` |
| GET | `/campanas` | Publico | `donate` con lista de campanas |
| POST | `/campanas` | Autenticado + CSRF | Crea campana y redirige |
| POST | `/campanas/eliminar/{id}` | Autenticado + CSRF | Elimina campana y redirige |
| POST | `/registrar` | Publico + CSRF | Registra usuario |
| POST | `/login/google` | Google OAuth | Login con Google |

### API REST

| Metodo | Ruta | Acceso | Respuesta |
|---|---|---|---|
| GET | `/api/donaciones` | Publico | Lista de donaciones |
| POST | `/api/donaciones` | Autenticado + CSRF | 201 con mensaje y entidad |
| GET | `/api/solicitudes` | Publico | Lista de solicitudes |
| POST | `/api/solicitudes` | Autenticado + CSRF | 201 con mensaje y entidad |
| GET | `/api/testimonios` | Publico | Lista de testimonios |
| POST | `/api/testimonios` | Publico + CSRF | 201 con entidad |
| PUT | `/api/testimonios/{id}` | Publico + CSRF | Entidad actualizada |
| DELETE | `/api/testimonios/{id}` | Publico + CSRF | 204 |
| GET | `/api/usuario/actual` | Autenticado | Usuario actual |

Notas:
- **No existe `/api/campanas`.** El `fetch` de `static/JS/index.js` esta comentado.
- **No existe entidad `Evento`.** La seccion de eventos de `donate.html` es HTML estatico.
- `esAdmin` en `CampanaController` realmente significa "usuario autenticado"; no valida rol administrador.
- `PUT` y `DELETE` de testimonios no estan restringidos por autenticacion en `SecurityConfig`; solo CSRF los protege.

## 7. Seguridad

- Form login en `/login` con parametros `email` y `pass`.
- Google OAuth2 configurado mediante `app.google.client-id`.
- CSRF habilitado para todas las peticiones que modifican estado.
- Todas las plantillas principales incluyen las meta etiquetas `_csrf` y `_csrf_header`.
- `csrf.js` expone `csrfHeaders()` para peticiones fetch.
- `POST /campanas/**`, `POST /api/donaciones` y `POST /api/solicitudes` requieren autenticacion.
- `SecurityConfig` no tiene reglas explicitas para `PUT`/`DELETE` de la API de testimonios.
- La contrasena de Neon esta configurada como valor por defecto en `application.properties` mediante variables de entorno. El valor real no se documenta aqui y el usuario pidio no modificarlo en esta rama.
- `ApiExceptionHandler` responde JSON para errores de validacion (`400` con mapa campo -> mensaje), `404` para `NotFoundException`, `400` para `BusinessException` y `500` para errores no controlados.

## 8. Modelo de datos

| Entidad | Tabla | Campos principales |
|---|---|---|
| `Usuario` | `usuarios` | `email` y `nombre` unicos, `password` BCrypt, `rol` (`USER`) |
| `Donacion` | `donaciones` | `codigoLote` unico, donante, producto, cantidad, estado, fecha |
| `SolicitudAyuda` | `solicitudes_ayuda` | solicitante, tipo de ayuda, descripcion, estado, fecha |
| `Campana` | `campanas` | titulo, descripcion, duracion, porcentaje, ubicacion, beneficiarios, meta, fechaCreacion |
| `Testimonio` | `testimonios` | nombre, rol, comentario, fecha |

## 9. Tests y verificacion

### Tests automatizados

```bash
./mvnw test
```

Resultado al cierre del refactor: **6 tests, 0 fallos, 0 errores**.

Cobertura actual:
- Contexto de Spring Boot carga correctamente.
- `CampanaService`: normalizacion de duracion (`15` -> `15 dias`) y de meta (`500 abrigos` -> `Meta: 500 abrigos`).
- `DonacionService`: estado `Registrado` por defecto.
- `UsuarioService`: email duplicado, contrasenas distintas y registro con BCrypt.

No hay tests de controllers ni de la API REST todavia.

### Compilacion y empaquetado

```bash
./mvnw -q -DskipTests package
```

Genera `target/proyectofinal-0.0.1-SNAPSHOT.jar`.

### Smoke test realizado

Se levanto el jar contra Neon y se verificaron estas rutas con respuesta `200`:

```text
/kallpa-solidary
/login
/registro
/reportes
/sobre-nosotros
/solicitudes
/donaciones
/campanas
/api/testimonios
/api/donaciones
/api/solicitudes
```

Tambien se verifico que no quedaran errores en el log. `/api/campanas` devuelve `404` porque no existe.

## 10. Trabajo realizado (cronologia)

### Etapa inicial (antes de esta bitacora)

- Proyecto Spring Boot con controladores, plantillas y conexion a Neon.
- Entidades y repositorios iniciales de donaciones y solicitudes.
- Vistas de inicio, donaciones, solicitudes, reportes, login y registro.
- Integracion inicial con Google OAuth.

### Refactor de arquitectura

- Se reorganizaron paquetes: `model/` -> `entity/`, `SecurityConfig` y `GlobalControllerAdvice` -> `config/`.
- Se crearon `Campana` y `Testimonio` como entidades persistidas.
- Se crearon sus repositorios, services y controllers.
- Se agregaron DTOs `Request` con validaciones.
- Se agregaron `BusinessException`, `NotFoundException` y `ApiExceptionHandler`.
- Se creo `ReporteService` y `DataInitializer`.
- Se ajustaron plantillas y JavaScript para consumir los datos del backend.
- Se renombro la tabla `"donciones"` a `"donaciones"` mediante la entidad `Donacion`.
- Se conservaron las rutas existentes.

### Eliminacion de DTOs `Response`

- Se eliminaron las cinco clases `*Response`.
- Los services ahora devuelven entidades (`Campana`, `Donacion`, `SolicitudAyuda`, `Testimonio`, `Usuario`).
- Los controllers REST devuelven entidades directamente.
- Se agrego `@JsonIgnore` a `Usuario.password`.
- Se actualizaron los tests de servicios y `ARQUITECTURA.md`.

### Correccion de CSRF en plantillas

- Se detecto un error al renderizar `/campanas`: `Cannot create a session after the response has been committed`.
- Se agregaron las meta etiquetas `_csrf` y `_csrf_header` en `donate.html`, `donaciones.html`, `solicitudes.html`, `about.html` y `reportes.html`.
- Se verifico que las ocho plantillas actuales tengan las meta etiquetas.
- El smoke test posterior paso sin errores.

### Documentacion y Git

- Se reescribio `ARQUITECTURA.md` con la arquitectura real.
- Se creo la rama `refactor/arquitectura-por-capas`.
- Se hizo el commit `5b55b2b` con 60 archivos (`1892` inserciones, `730` eliminaciones).
- Se subio la rama a GitHub sin tocar `main`.
- Se creo este archivo `HISTORIAL.md`.

## 11. Pendientes y proximos pasos

### API JSON de campanas (recomendado, no implementado)

Objetivo: probar GET/POST con JSON y validar entradas. Plan propuesto:

- Crear `CampanaApiController` en `/api/campanas` con GET, GET por id, POST, PUT y DELETE.
- Reutilizar `CampanaService` y `CampanaRequest`; no crear DTOs `Response`.
- Devolver la entidad directamente (201 en POST, 204 en DELETE).
- Reforzar validaciones de `CampanaRequest`: `@Size`, rango de duracion, etc.
- Agregar manejo de `HttpMessageNotReadableException` para JSON malformado (`400`).
- Definir estrategia de seguridad para la API: mantener CSRF con token o usar HTTP Basic y eximir `/api/**` de CSRF.
- Agregar `CampanaApiControllerTest` con MockMvc y H2.

### Otros pendientes

- No hay CRUD JSON de campanas; `index.js` tiene el fetch comentado.
- No existe entidad `Evento`; los eventos son estaticos.
- No hay tests de controllers ni de seguridad.
- Revisar permisos de `PUT`/`DELETE` de testimonios.
- Revisar que `POST /campanas/eliminar/{id}` solo deberia permitir administradores.
- Evaluar rotacion del password de Neon por estar en el historial de Git. El usuario pidio no modificarlo en esta rama.
- Crear Pull Request cuando el usuario lo autorice.

## 12. Como ejecutar y probar

```bash
# Tests
./mvnw test

# Ejecutar en desarrollo
./mvnw spring-boot:run

# Empaquetar y ejecutar el jar
./mvnw -q -DskipTests package
java -jar target/proyectofinal-0.0.1-SNAPSHOT.jar
```

Abrir `http://localhost:8090/kallpa-solidary`.

Para probar formularios autenticados:
1. Registrarse en `/registro`.
2. Iniciar sesion en `/login`.
3. Ir a `/campanas` y usar el modal "Registrar Nueva Campana".

Para peticiones JSON a la API existente se necesita cookie de sesion y token CSRF, salvo que se implemente la exencion para `/api/**`.

## 13. Registro de cambios

| Fecha | Cambio | Detalle |
|---|---|---|
| 2026-10-25 | Refactor de arquitectura | Paquetes, entidades, services, controllers y rutas conservadas. |
| 2026-10-25 | Eliminacion de DTOs `Response` | Services y controllers REST devuelven entidades; `@JsonIgnore` en password. |
| 2026-10-25 | Correccion de CSRF | Meta etiquetas en plantillas; smoke test sin errores. |
| 2026-10-25 | Tests de servicios | 6 tests aprobados con `./mvnw test`. |
| 2026-10-25 | Documentacion y Git | `ARQUITECTURA.md`, rama `refactor/arquitectura-por-capas`, commit `5b55b2b`, push de la rama, `main` intacta. |
| 2026-10-25 | Creacion de `HISTORIAL.md` | Bitacora del proyecto y regla de actualizacion obligatoria. |

## 14. Protocolo para futuras modificaciones

Antes de terminar cualquier tarea:

1. Ejecutar los tests correspondientes (`./mvnw test`) si se toco codigo.
2. Actualizar este archivo:
   - fecha y estado de Git;
   - seccion afectada;
   - nueva fila en "Registro de cambios";
   - pendientes si quedo algo incompleto.
3. Si el cambio afecta la arquitectura, actualizar tambien `ARQUITECTURA.md`.
4. No commitear secretos ni pegar valores reales de credenciales.
5. Si se crea una rama o commit nuevo, reflejar rama y hash.
