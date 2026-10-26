# Arquitectura del Proyecto Kallpa Solidary

Guía de referencia de la estructura, convenciones y decisiones técnicas de este proyecto Spring Boot.

## Stack tecnológico

| Elemento | Versión / Tecnología |
|---|---|
| Java | 21 |
| Spring Boot | 4.1.1 |
| Build | Maven (`pom.xml`, `mvnw`) |
| Persistencia | Spring Data JPA + Hibernate 7 |
| Base de datos | PostgreSQL en Neon (H2 en memoria para tests) |
| Web | Spring MVC + Thymeleaf |
| Seguridad | Spring Security (form login + Google OAuth2) |
| Validación | `spring-boot-starter-validation` (Jakarta Validation) |
| Utilidades | `thymeleaf-extras-springsecurity6`, `google-api-client`, DevTools |
| Pruebas | JUnit 5 + Mockito + `spring-boot-starter-test` |

## Estructura de paquetes

```
src/main/java/pe/utp/proyectofinal/
├── ProyectofinalApplication.java       # Clase main (@SpringBootApplication)
├── config/
│   ├── ApiExceptionHandler.java        # Errores de controladores REST
│   ├── DataInitializer.java            # Datos iniciales de campañas y testimonios
│   ├── GlobalControllerAdvice.java     # Atributos comunes de las vistas MVC
│   └── SecurityConfig.java             # Cadena de filtros, login y CSRF
├── controller/
│   ├── AuthController.java             # Registro de usuarios
│   ├── CampanaController.java          # Vista y formularios de campañas
│   ├── DonacionController.java         # API REST de donaciones
│   ├── GoogleAuthController.java       # Login con Google
│   ├── SolicitudAyudaController.java   # API REST de solicitudes
│   ├── TestimoniosController.java      # API REST de testimonios
│   ├── UserController.java             # Usuario autenticado
│   └── VistaController.java            # Rutas de plantillas Thymeleaf
├── dto/
│   ├── *Request.java                   # Entrada validada (formularios y API)
│   ├── GoogleUserDTO.java              # Datos del token de Google
│   └── ReporteDTO.java                 # KPIs y movimientos de reportes
├── entity/
│   ├── Campana.java
│   ├── Donacion.java
│   ├── SolicitudAyuda.java
│   ├── Testimonio.java
│   └── Usuario.java
├── exception/
│   ├── BusinessException.java          # Regla de negocio con código
│   └── NotFoundException.java          # Recurso inexistente (404)
├── repository/                         # Interfaces JpaRepository
└── service/                            # Lógica de negocio y conversión Request/Entity

src/main/resources/
├── application.properties              # Configuración externalizada
├── static/JS/                          # JavaScript de las vistas
├── static/CSS/                         # Estilos
└── templates/                          # Plantillas Thymeleaf

src/test/
├── java/pe/utp/proyectofinal/          # Context load y tests de servicios
└── resources/application.properties    # H2 en memoria para tests
```

## Flujo de una petición

### Vistas Thymeleaf (MVC)

```
Navegador
   ↓
Controller MVC (@Controller)
   ↓
Service → Repository → Entity
   ↓
Model + nombre de plantilla
   ↓
Thymeleaf renderiza HTML
```

### API REST

```
Cliente HTTP (JSON)
   ↓
Controller REST (@RestController) → valida el Request con @Valid
   ↓
Service → reglas de negocio y conversión Request/Entity
   ↓
Repository → JpaRepository
   ↓
Entity → tabla PostgreSQL
   ↓
Entity serializada como JSON
```

Regla principal: ningún Controller accede directamente a un Repository; siempre pasa por un Service. La salida de la API se hace con la entidad JPA; la entrada se recibe con un DTO `Request`.

## Roles de cada capa

### `controller/`

Responsabilidades:
- Definir rutas HTTP y recibir parámetros, path variables, formularios o JSON.
- Activar validación con `@Valid`.
- Delegar la lógica al Service.
- Elegir entre respuesta HTML (`@Controller`) o JSON (`@RestController`).

Convenciones:
- Inyección de dependencias por constructor.
- API REST bajo `/api/<recurso>`.
- `POST` de creación devuelve `201 CREATED`; `DELETE` devuelve `204 NO_CONTENT`.
- Los controladores MVC devuelven el nombre de la plantilla o un `redirect:`.

### `dto/`

Responsabilidades:
- Recibir y validar los datos de entrada de formularios y de la API.
- Evitar que los formularios y el JSON de entrada dependan de la entidad.

Convenciones:
- Sufijo `Request` para entrada. No se usan DTOs `Response`: la salida REST devuelve la entidad.
- POJOs con getters y setters, sin lógica de negocio.
- Validaciones Jakarta (`@NotBlank`, `@NotNull`, `@Positive`, `@Min`, `@Size`) con mensajes en español.
- `RegistroRequest` usa `password` y `confirmPassword`; la coincidencia se valida en `UsuarioService`.

### `entity/`

Responsabilidades:
- Mapear las tablas de PostgreSQL.

Convenciones:
- `@Entity` + `@Table(name = "...")`.
- `@Id` con `@GeneratedValue(strategy = GenerationType.IDENTITY)`.
- Columnas obligatorias con `nullable = false` y longitud explícita.
- Fechas con `LocalDateTime`, asignadas en el Service.
- Getters y setters manuales.
- La entidad se serializa directamente como JSON en la API REST.
- `Usuario.password` lleva `@JsonIgnore` para no exponer el hash en las respuestas.

Tablas actuales:

| Entidad | Tabla | Campos principales |
|---|---|---|
| `Usuario` | `usuarios` | `email` y `nombre` únicos, `password` con BCrypt, `rol` (`USER`) |
| `Donacion` | `donaciones` | `codigoLote` único, donante, producto, cantidad, estado, fecha |
| `SolicitudAyuda` | `solicitudes_ayuda` | solicitante, tipo de ayuda, descripción, estado, fecha |
| `Campana` | `campanas` | título, descripción, duración, porcentaje, ubicación, beneficiarios, meta |
| `Testimonio` | `testimonios` | nombre, rol, comentario, fecha |

> La tabla heredada `"donciones"` fue reemplazada por `"donaciones"` (`Donacion`). Los datos de la tabla antigua no se migran automáticamente.

### `repository/`

Responsabilidades:
- Extender `JpaRepository<Entidad, Long>`.
- Declarar query methods derivados.

Convenciones:
- Una interfaz por entidad.
- Ejemplos: `UsuarioRepository.existsByEmail`, `findByEmail` (devuelve `Optional`).

### `service/`

Responsabilidades:
- Implementar reglas de negocio y casos de uso.
- Orquestar repositorios.
- Convertir el `Request` a Entity y devolver la Entity guardada.
- Asignar valores por defecto y normalizar texto.

Convenciones:
- Anotado con `@Service`.
- Inyección por constructor.
- Métodos típicos: `listar`, `buscar`, `crear`/`registrar`, `actualizar`, `eliminar`.
- Búsquedas con `findById(...).orElseThrow(() -> new NotFoundException(...))`.
- Reglas de negocio con `BusinessException(code, message)`.

Reglas implementadas:
- `UsuarioService`: email único (sin distinguir mayúsculas), contraseñas coincidentes, BCrypt, rol `USER`.
- `DonacionService`: genera `codigoLote` si no se envía y usa estado `Registrado` por defecto.
- `SolicitudAyudaService`: registra con estado `Registrada`.
- `CampanaService`: normaliza duración (`15` → `15 días`) y meta (`500` → `Meta: 500`).
- `ReporteService`: calcula KPIs y combina donaciones y solicitudes como movimientos.

### `config/`

- `SecurityConfig`: rutas públicas, rutas autenticadas, form login, OAuth2 con Google, logout y CSRF.
- `ApiExceptionHandler`: `@RestControllerAdvice` limitado a controladores REST; traduce validación, `BusinessException` y `NotFoundException`.
- `GlobalControllerAdvice`: agrega datos comunes al modelo de las vistas.
- `DataInitializer`: siembra campañas y testimonios solo cuando las tablas están vacías.

### `exception/`

- `BusinessException`: error de negocio con un código estable para la vista/API.
- `NotFoundException`: recurso inexistente; se traduce a `404`.

## Seguridad

- Rutas públicas: inicio, login, registro, campañas (GET), donaciones (GET), solicitudes (GET), testimonios, reportes, recursos estáticos y `/api/testimonios` (GET).
- Requieren autenticación: `/donar-ahora`, `/ver-voluntario`, `/donar-bienes`, `/eventos`, `/ver-campanas`, `POST /campanas/**`, `POST /api/donaciones`, `POST /api/solicitudes`.
- Login: formulario propio en `/login` y OAuth2 con Google.
- Contraseñas: BCrypt.
- CSRF: **habilitado**. Los formularios usan `th:action`; JavaScript envía el token con `csrfHeaders()` desde `static/JS/csrf.js`.
- Todas las plantillas incluyen las meta etiquetas `_csrf` y `_csrf_header`. Es importante mantenerlas: generan el token al inicio de la respuesta y evitan errores al renderizar formularios en páginas largas.

## Rutas principales

| Método | Ruta | Tipo | Descripción |
|---|---|---|---|
| GET | `/kallpa-solidary` | MVC | Página de inicio |
| GET | `/sobre-nosotros` | MVC | Información institucional |
| GET | `/campanas` | MVC | Listado y formulario de campañas |
| POST | `/campanas` | MVC | Crear campaña (autenticado) |
| POST | `/campanas/eliminar/{id}` | MVC | Eliminar campaña (autenticado) |
| GET | `/solicitudes` | MVC | Vista de solicitudes |
| GET | `/donaciones` | MVC | Vista de donaciones |
| GET | `/reportes` | MVC | KPIs y movimientos |
| GET | `/login`, `/registro` | MVC | Autenticación y registro |
| POST | `/registrar` | MVC | Registrar usuario |
| POST | `/login/google` | REST | Login con token de Google |
| GET | `/api/usuario/actual` | REST | Usuario autenticado |
| GET/POST | `/api/donaciones` | REST | Listar y registrar donaciones |
| GET/POST | `/api/solicitudes` | REST | Listar y registrar solicitudes |
| GET/POST | `/api/testimonios` | REST | Listar y crear testimonios |
| PUT/DELETE | `/api/testimonios/{id}` | REST | Actualizar y eliminar testimonios |

## Configuración

`src/main/resources/application.properties` usa variables de entorno con valores por defecto para desarrollo:

```properties
server.port=${SERVER_PORT:8090}
spring.datasource.url=${DB_URL:...}
spring.datasource.username=${DB_USERNAME:...}
spring.datasource.password=${DB_PASSWORD:...}
spring.jpa.hibernate.ddl-auto=${JPA_DDL_AUTO:update}
spring.jpa.open-in-view=false
app.google.client-id=${GOOGLE_CLIENT_ID:...}
```

Recomendaciones:
- Definir `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` y `GOOGLE_CLIENT_ID` en el entorno.
- No versionar contraseñas reales; rotar cualquier credencial que haya estado en el repositorio.
- En producción usar `JPA_DDL_AUTO=validate` y migraciones controladas (Flyway o Liquibase).

## Pruebas

- `ProyectofinalApplicationTests`: carga el contexto completo con H2.
- `UsuarioServiceTest`: email duplicado, contraseñas distintas y registro exitoso.
- `DonacionServiceTest`: estado por defecto y registro.
- `CampanaServiceTest`: normalización de duración y meta.
- Los tests usan `src/test/resources/application.properties` con H2 en memoria; no tocan Neon.

Comando:

```bash
./mvnw test
```

## Checklist para agregar una nueva entidad

1. **Entity**: crear la clase en `entity/` con `@Entity`, `@Table`, `@Id` y columnas.
2. **DTO**: crear `XxxRequest` con las validaciones de entrada. No crear `XxxResponse`.
3. **Repository**: crear `XxxRepository extends JpaRepository<Xxx, Long>`.
4. **Service**: crear `XxxService` con `@Service`, inyección por constructor y métodos `listar`, `buscar`, `crear`, `actualizar`, `eliminar`.
5. **Controller**: crear el controlador MVC o REST según corresponda; nunca acceder al Repository.
6. **Errores**: reutilizar `BusinessException`, `NotFoundException` y `ApiExceptionHandler`.
7. **Plantilla y CSRF**: si hay formularios, usar `th:action` y mantener las meta etiquetas `_csrf`.
8. **Tests**: agregar pruebas de las reglas de negocio en `src/test/java/.../service/`.

## Resumen de responsabilidades

| Capa | Anotación | Entrada/Salida | Lógica de negocio |
|---|---|---|---|
| Controller MVC | `@Controller` | Formularios / nombre de plantilla | No |
| Controller REST | `@RestController` | Request / Entity | No |
| DTO | — | JSON o formulario de entrada | No |
| Service | `@Service` | Request / Entity | Sí |
| Repository | `JpaRepository` | Entity | No |
| Entity | `@Entity` | — | No |
| Config | `@Configuration`, `@RestControllerAdvice` | Configuración / errores | Transversal |
