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

import pe.utp.proyectofinal.dto.DonacionRequest;
import pe.utp.proyectofinal.entity.Donacion;
import pe.utp.proyectofinal.service.DonacionService;

@RestController
@RequestMapping("/api/donaciones")
@CrossOrigin(origins = "*")
public class DonacionController {

    private final DonacionService donacionService;

    public DonacionController(DonacionService donacionService) {
        this.donacionService = donacionService;
    }

    /**
     * GET /api/donaciones
     * Permite listar todas las donaciones o filtrar por query params:
     * ?campanaId=1&estado=PENDIENTE&donante=Juan&producto=Arroz&buscar=texto
     */
    @GetMapping
    public List<Donacion> listarDonaciones(
            @RequestParam(required = false) Long campanaId,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String donante,
            @RequestParam(required = false) String producto,
            @RequestParam(required = false) String buscar) {

        boolean tieneFiltro = campanaId != null
                || (estado != null && !estado.isBlank())
                || (donante != null && !donante.isBlank())
                || (producto != null && !producto.isBlank())
                || (buscar != null && !buscar.isBlank());

        if (tieneFiltro) {
            return donacionService.buscarFiltrado(campanaId, estado, donante, producto, buscar);
        }
        return donacionService.listar();
    }

    /**
     * GET /api/donaciones/{id}
     * Obtiene una donación por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Donacion> buscarDonacion(@PathVariable Long id) {
        Donacion donacion = donacionService.buscar(id);
        return ResponseEntity.ok(donacion);
    }

    /**
     * POST /api/donaciones
     * Registra una nueva donación con JSON en el cuerpo.
     * Retorna HTTP 201 Created con cuerpo informativo.
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> registrarDonacion(
            @RequestBody(required = false) DonacionRequest request) {
        Donacion nuevaDonacion = donacionService.registrar(request);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("exito", true);
        response.put("mensaje", "Donación y lote registrados correctamente");
        response.put("data", nuevaDonacion);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/donaciones (Soporte para form-data y x-www-form-urlencoded de Postman/Thunder Client)
     */
    @PostMapping(consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Map<String, Object>> registrarDonacionForm(@ModelAttribute DonacionRequest request) {
        return registrarDonacion(request);
    }

    /**
     * PUT /api/donaciones/{id}
     * Actualiza una donación existente. Admite actualización total o parcial de campos.
     * Retorna HTTP 200 OK con los datos actualizados.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarDonacion(
            @PathVariable Long id,
            @RequestBody(required = false) DonacionRequest request) {
        Donacion donacionActualizada = donacionService.actualizar(id, request);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("exito", true);
        response.put("mensaje", "Donación con ID " + id + " actualizada correctamente");
        response.put("data", donacionActualizada);

        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/donaciones/{id} (Soporte para form-data y x-www-form-urlencoded de Postman/Thunder Client)
     */
    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Map<String, Object>> actualizarDonacionForm(
            @PathVariable Long id,
            @ModelAttribute DonacionRequest request) {
        return actualizarDonacion(id, request);
    }

    /**
     * DELETE /api/donaciones/{id}
     * Elimina una donación y sus registros asociados de recepción.
     * Retorna HTTP 200 OK con confirmación clara en el cuerpo para Postman/Thunder Client.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarDonacion(@PathVariable Long id) {
        donacionService.eliminar(id);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("exito", true);
        response.put("mensaje", "Donación con ID " + id + " eliminada correctamente");
        response.put("id", id);

        return ResponseEntity.ok(response);
    }
}
