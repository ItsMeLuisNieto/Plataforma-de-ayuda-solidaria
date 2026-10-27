package pe.utp.proyectofinal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pe.utp.proyectofinal.dto.DonacionRequest;
import pe.utp.proyectofinal.entity.Donacion;
import pe.utp.proyectofinal.service.DonacionService;

@RestController
@RequestMapping("/api/donaciones")
public class DonacionController {

    private final DonacionService donacionService;

    public DonacionController(DonacionService donacionService) {
        this.donacionService = donacionService;
    }

    @GetMapping
    public List<Donacion> listarDonaciones() {
        return donacionService.listar();
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> registrarDonacion(
            @Valid @RequestBody DonacionRequest request) {
        Donacion nuevaDonacion = donacionService.registrar(request);

        Map<String, Object> response = new HashMap<>();
        response.put("exito", true);
        response.put("mensaje", "Donación y lote registrados correctamente");
        response.put("data", nuevaDonacion);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/yape")
    public ResponseEntity<Map<String, Object>> registrarDonacionYape(
            @RequestBody DonacionRequest request) {
        try {
            Donacion nuevaDonacion = donacionService.registrarDonacionYape(request);

            Map<String, Object> response = new HashMap<>();
            response.put("exito", true);
            response.put("mensaje", "Tu donación quedó registrada y está pendiente de verificación por el administrador. Solo después de ser confirmada, se reflejará en los reportes.");
            response.put("data", nuevaDonacion);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("exito", false);
            response.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("exito", false);
            response.put("mensaje", "Ocurrió un error al registrar la donación. Verifica los datos e intenta nuevamente.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/admin/{id}/confirmar")
    public ResponseEntity<Map<String, Object>> confirmarDonacion(@PathVariable Long id, Authentication authentication) {
        if (!esAdmin(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            Donacion donacion = donacionService.confirmarDonacion(id);
            Map<String, Object> response = new HashMap<>();
            response.put("exito", true);
            response.put("mensaje", "Donación confirmada correctamente");
            response.put("data", donacion);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("exito", false);
            response.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/admin/{id}/rechazar")
    public ResponseEntity<Map<String, Object>> rechazarDonacion(@PathVariable Long id, @RequestBody Map<String, String> body, Authentication authentication) {
        if (!esAdmin(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            String motivo = body.getOrDefault("motivo", "No especificado");
            Donacion donacion = donacionService.rechazarDonacion(id, motivo);
            Map<String, Object> response = new HashMap<>();
            response.put("exito", true);
            response.put("mensaje", "Donación rechazada");
            response.put("data", donacion);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("exito", false);
            response.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    private boolean esAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        if ("anonymousUser".equals(authentication.getName())) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }
}
