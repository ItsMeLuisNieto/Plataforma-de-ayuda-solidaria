package pe.utp.proyectofinal.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pe.utp.proyectofinal.dto.TestimonioRequest;
import pe.utp.proyectofinal.entity.Testimonio;
import pe.utp.proyectofinal.service.TestimonioService;

@RestController
@RequestMapping("/api/testimonios")
@CrossOrigin(origins = "*")
public class TestimoniosController {

    private final TestimonioService testimonioService;

    public TestimoniosController(TestimonioService testimonioService) {
        this.testimonioService = testimonioService;
    }

    /**
     * GET /api/testimonios
     * Retorna la lista de testimonios.
     * Permite filtrar por parámetros opcionales: ?buscar=gracias&rol=Donante&nombre=Maria
     */
    @GetMapping
    public List<Testimonio> obtenerTestimonios(
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false) String rol,
            @RequestParam(required = false) String nombre) {

        boolean tieneFiltro = (buscar != null && !buscar.isBlank())
                || (rol != null && !rol.isBlank())
                || (nombre != null && !nombre.isBlank());

        if (tieneFiltro) {
            return testimonioService.buscarFiltrado(buscar, rol, nombre);
        }
        return testimonioService.listar();
    }

    /**
     * GET /api/testimonios/{id}
     * Obtiene un testimonio específico por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Testimonio> obtenerTestimonio(@PathVariable Long id) {
        Testimonio testimonio = testimonioService.buscar(id);
        return ResponseEntity.ok(testimonio);
    }

    /**
     * POST /api/testimonios
     * Registra un nuevo testimonio mediante JSON.
     * Retorna HTTP 201 Created con respuesta descriptiva para Postman/Thunder Client.
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> agregarTestimonio(
            @RequestBody(required = false) TestimonioRequest request) {
        Testimonio nuevoTestimonio = testimonioService.crear(request);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("exito", true);
        response.put("mensaje", "Testimonio registrado correctamente");
        response.put("data", nuevoTestimonio);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/testimonios (Soporte para form-data y x-www-form-urlencoded de Postman/Thunder Client)
     */
    @PostMapping(consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Map<String, Object>> agregarTestimonioForm(@ModelAttribute TestimonioRequest request) {
        return agregarTestimonio(request);
    }

    /**
     * PUT /api/testimonios/{id}
     * Actualiza un testimonio existente. Admite actualización total o parcial de campos.
     * Retorna HTTP 200 OK con los datos modificados.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarTestimonio(
            @PathVariable Long id,
            @RequestBody(required = false) TestimonioRequest request) {
        Testimonio testimonioActualizado = testimonioService.actualizar(id, request);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("exito", true);
        response.put("mensaje", "Testimonio con ID " + id + " actualizado correctamente");
        response.put("data", testimonioActualizado);

        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/testimonios/{id} (Soporte para form-data y x-www-form-urlencoded de Postman/Thunder Client)
     */
    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Map<String, Object>> actualizarTestimonioForm(
            @PathVariable Long id,
            @ModelAttribute TestimonioRequest request) {
        return actualizarTestimonio(id, request);
    }

    /**
     * DELETE /api/testimonios/{id}
     * Elimina un testimonio por su ID.
     * Retorna HTTP 200 OK con mensaje explícito en lugar de un cuerpo vacío.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarTestimonio(@PathVariable Long id) {
        testimonioService.eliminar(id);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("exito", true);
        response.put("mensaje", "Testimonio con ID " + id + " eliminado correctamente");
        response.put("id", id);

        return ResponseEntity.ok(response);
    }
}
