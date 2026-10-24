package pe.utp.proyectofinal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.utp.proyectofinal.model.SolicitudAyuda;
import pe.utp.proyectofinal.repository.SolicitudAyudaRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudAyudaController {
    @Autowired
    private SolicitudAyudaRepository solicitudAyudaRepository;

    @GetMapping
    public ResponseEntity<List<SolicitudAyuda>> listarSolicitudes() {
        List<SolicitudAyuda> lista = solicitudAyudaRepository.findAll();
        return ResponseEntity.ok(lista);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> guardarSolicitud(@RequestBody SolicitudAyuda solicitud) {
        Map<String, Object> respuesta = new HashMap<>();
        try {
            solicitud.setEstado("Registrada"); // Cumple el criterio de aceptación del RF-09
            SolicitudAyuda guardada = solicitudAyudaRepository.save(solicitud);

            respuesta.put("exito", true);
            respuesta.put("mensaje", "Solicitud de ayuda registrada correctamente.");
            respuesta.put("data", guardada);

            return ResponseEntity.status(HttpStatus.SC_CREATED).body(respuesta);

        } catch (Exception e) {
            respuesta.put("exito", false);
            respuesta.put("mensaje", "Error al procesar la solicitud: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body(respuesta);
        }
    }

}
