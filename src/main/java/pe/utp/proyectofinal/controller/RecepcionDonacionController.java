package pe.utp.proyectofinal.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.utp.proyectofinal.entity.RecepcionDonacion;
import pe.utp.proyectofinal.service.RecepcionDonacionService;

@RestController
@RequestMapping("/api/recepcion-donaciones")
@CrossOrigin(origins = "*")
public class RecepcionDonacionController {

    private final RecepcionDonacionService recepcionDonacionService;

    public RecepcionDonacionController(RecepcionDonacionService recepcionDonacionService) {
        this.recepcionDonacionService = recepcionDonacionService;
    }

    @GetMapping
    public List<RecepcionDonacion> listar() {
        return recepcionDonacionService.listar();
    }

    @GetMapping("/pendientes")
    public List<RecepcionDonacion> listarPendientes() {
        return recepcionDonacionService.listarPendientes();
    }

    @PostMapping("/{id}/confirmar")
    public ResponseEntity<Map<String, Object>> confirmar(@PathVariable Long id, Authentication authentication) {
        RecepcionDonacion recepcion = recepcionDonacionService.confirmar(id, authentication);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("exito", true);
        response.put("mensaje", "Donación confirmada correctamente");
        response.put("data", recepcion);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/rechazar")
    public ResponseEntity<Map<String, Object>> rechazar(@PathVariable Long id,
                                                        @RequestBody(required = false) Map<String, String> body,
                                                        Authentication authentication) {
        String motivo = body != null ? body.get("motivo") : null;
        RecepcionDonacion recepcion = recepcionDonacionService.rechazar(id, motivo, authentication);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("exito", true);
        response.put("mensaje", "Donación rechazada");
        response.put("data", recepcion);
        return ResponseEntity.ok(response);
    }
}
