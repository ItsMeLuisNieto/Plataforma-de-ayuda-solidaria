package pe.utp.proyectofinal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.utp.proyectofinal.model.Donacion;
import pe.utp.proyectofinal.repository.DonacionRepository;

@RestController 
@RequestMapping ("/api/donaciones")
public class DonacionController {
    @Autowired
    private DonacionRepository donacionRepository;

    @GetMapping
    public List<Donacion> listarDonaciones(){
        return donacionRepository.findAll();
    }

    @PostMapping 
    public ResponseEntity<?> registrarDonacion(@RequestBody Donacion donacion){
        try{
            Donacion nuevaDonacion = donacionRepository.save(donacion);

            Map<String, Object> response = new HashMap<>();
            response.put("exito", true);
            response.put("mensaje","Donación y lote registrados correctamente");
            response.put("data", nuevaDonacion);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("exito", false);
            errorResponse.put("mensaje", "Error al registrar la donación: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        

    }

}
