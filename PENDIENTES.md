# Pendientes

Lista de tareas pendientes para hacer después de recargar los tokens.

---

## 1. Módulo de Donación Monetaria (Yape)

Separar las donaciones en dos módulos: el material ya existente (`donaciones`) y uno nuevo monetario, porque necesita campos propios que el módulo material no va a usar.

**Decisiones ya tomadas:**
- Entidad/tabla/API: `DonacionMonetaria` / `donaciones_monetarias` / `/api/donaciones-monetarias`.
- Registro del yapeo **requiere sesión** (igual que testimonios).
- Conteo para reportes **con verificación de ADMIN** (REGISTRADO → VERIFICADO).
- Vista de gestión propia para ADMIN en el dropdown Gestión.
- Yapeos VERIFICADO aparecen en tabla de movimientos y exportes (Excel/PDF).

**Plan detallado:**

### BD — tabla `donaciones_monetarias` (auto con `ddl-auto=update`)
- `id_donaciones_monetarias` PK IDENTITY
- `nombre_completo` VARCHAR(150) NOT NULL
- `monto` NUMERIC(10,2) NOT NULL
- `codigo_operacion` VARCHAR(3) NOT NULL (código de 3 dígitos que da Yape)
- `fecha` DATE NOT NULL (la del yapeo, la indica el donante)
- `fecha_registro` TIMESTAMP NOT NULL (`@PrePersist`)
- `estado` VARCHAR(30) default `REGISTRADO`
- `fecha_verificacion` TIMESTAMP
- `verificado_por` VARCHAR(100)
- **Sin FK a campañas** (el QR de Yape es global de "Grupo Kallpa Solidary").

### Backend (patrón del módulo material)
- `model/DonacionMonetaria.java` + `repository/DonacionMonetariaRepository.java` + `service/DonacionMonetariaService.java` + `controller/DonacionMonetariaController.java`
- `dto/DonacionMonetariaRequest.java` con validaciones Jakarta: nombre @NotBlank, monto @DecimalMin("0.01") @Digits(8,2), código @Pattern `^[0-9]{3}$`, fecha @PastOrPresent.
- API: GET lista, GET /{id}, POST (201, `{exito, mensaje, data}`), **POST /{id}/verificar** (ADMIN, `BusinessException` si ya verificado — patrón envíos), DELETE /{id} (204, ADMIN).
- `SecurityConfig`: agregar `POST /api/donaciones-monetarias` → `authenticated()` **antes** de la regla ADMIN genérica (patrón testimonios). Verificar/eliminar caen en reglas ADMIN existentes.
- `Donacion` (material) queda intacto; sin cambios en cascadas de `CampanaService` (no hay FK).

### Frontend
- **Modal "Donar dinero"** en `campanas.html`, bajo el QR de Yape: botón "Ya yapeé — registrar mi donación" que despliega formulario (nombre completo, monto S/, código de 3 dígitos, fecha con default hoy).
- Anónimos: aviso "Inicia sesión para registrar tu yapeo" con link a `/login` (`sec:authorize`).
- Autenticados: POST con `csrfHeaders()` (csrf.js ya está incluido en campanas.html), modal de éxito/error.
- **Vista admin** `/donaciones-monetarias` (clon del patrón envíos): tabla (donante, monto, código, fecha yapeo, fecha registro, estado badge REGISTRADO/VERIFICADO, verificado por) + botón **Verificar** + Eliminar. `VistaController` + plantilla + `static/JS/donaciones-monetarias.js` + ítem "Donaciones Monetarias" en el dropdown Gestión de las 8 plantillas.

### Reportes
- KPI "Recaudación Total" = `SUM(monto)` de yapeos **VERIFICADO** (0 si vacío; hoy está fijo en `BigDecimal.ZERO`).
- Texto del KPI: "por Yape y tarjetas" → "por Yape" (ya no hay tarjeta).
- Tabla de movimientos: yapeos VERIFICADO como filas — tipo "Monetaria", valor "S/ 150.00", campaña "Donación Yape", distrito "-", estado "VERIFICADO". Se refleja solo en vista, Excel y PDF (paralelo a materiales: reportes muestra solo CONFIRMADO).

### Seed + docs + verificación
- `DataInitializer`: ~5 yapeos de ejemplo si la tabla está vacía (3 VERIFICADO con `verificadoPor="admin@kallpa.local"`, 2 REGISTRADO; nombres reales, montos variados).
- `Manual.md` + `CONTEXTO_PROYECTO.md` actualizados.
- Compile + smoke tests en vivo: POST anónimo 401 JSON, USUARIO 201, validaciones 400, verificar 403 USUARIO / 200 ADMIN / re-verificar 400, KPI sumado en reportes, vistas, limpieza de datos de prueba.
