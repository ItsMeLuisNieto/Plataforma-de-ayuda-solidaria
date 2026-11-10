package pe.utp.proyectofinal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.utp.proyectofinal.model.EnvioDonacion;
import pe.utp.proyectofinal.service.EnvioDonacionService;

@RestController
@RequestMapping("/api/envio-donaciones")
public class EnvioDonacionController {

    private final EnvioDonacionService envioDonacionService;

    public EnvioDonacionController(EnvioDonacionService envioDonacionService) {
        this.envioDonacionService = envioDonacionService;
    }

    @GetMapping
    public List<EnvioDonacion> listar() {
        return envioDonacionService.listar();
    }

    @GetMapping("/{id}")
    public EnvioDonacion buscar(@PathVariable Long id) {
        return envioDonacionService.buscar(id);
    }

    @PostMapping("/{id}/enviar")
    public ResponseEntity<Map<String, Object>> enviar(@PathVariable Long id, Authentication authentication) {
        EnvioDonacion envio = envioDonacionService.enviar(id, authentication);
        Map<String, Object> response = new HashMap<>();
        response.put("exito", true);
        response.put("mensaje", "Envío de donación marcado como ENVIADO");
        response.put("data", envio);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        envioDonacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
