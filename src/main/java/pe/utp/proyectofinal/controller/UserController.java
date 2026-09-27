package pe.utp.proyectofinal.controller;

import pe.utp.proyectofinal.model.Usuario;
import pe.utp.proyectofinal.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    @Autowired 
    private UsuarioRepository usuarioRepository;

    @GetMapping("/api/usuario/actual")
    public Map<String, String> obtenerUsuarioActual(Authentication authentication) {
        Map<String, String> datos = new HashMap<>();
        
        // Verifica si hay un usuario logueado
        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            datos.put("email", authentication.getName()); 
            
            Usuario actual = usuarioRepository.findByEmail(authentication.getName());

            datos.put("nombre", actual.getNombre());
        } else {
            datos.put("error", "No autenticado");
        }
        
        return datos;
    }
}