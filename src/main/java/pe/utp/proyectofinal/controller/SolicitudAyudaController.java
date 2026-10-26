package pe.utp.proyectofinal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pe.utp.proyectofinal.dto.SolicitudAyudaRequest;
import pe.utp.proyectofinal.entity.SolicitudAyuda;
import pe.utp.proyectofinal.service.SolicitudAyudaService;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudAyudaController {

    private final SolicitudAyudaService solicitudAyudaService;

    public SolicitudAyudaController(SolicitudAyudaService solicitudAyudaService) {
        this.solicitudAyudaService = solicitudAyudaService;
    }

    @GetMapping
    public ResponseEntity<List<SolicitudAyuda>> listarSolicitudes() {
        return ResponseEntity.ok(solicitudAyudaService.listar());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> guardarSolicitud(
            @Valid @RequestBody SolicitudAyudaRequest request) {
        SolicitudAyuda guardada = solicitudAyudaService.registrar(request);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exito", true);
        respuesta.put("mensaje", "Solicitud de ayuda registrada correctamente.");
        respuesta.put("data", guardada);

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }
}
