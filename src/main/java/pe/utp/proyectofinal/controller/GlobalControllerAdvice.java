package pe.utp.proyectofinal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import pe.utp.proyectofinal.model.Usuario;
import pe.utp.proyectofinal.repository.UsuarioRepository;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // El nombre entre comillas es la variable que Thymeleaf reconocerá en cualquier HTML
    @ModelAttribute("nombreUsuario")
    public String inyectarNombreGlobal(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            String email = authentication.getName();
            Usuario usuario = usuarioRepository.findByEmail(email);
            
            if (usuario != null) {
                return usuario.getNombre();
            }
        }
        // Si no hay sesión o no se encuentra, devolvemos nulo y Thymeleaf simplemente no lo imprime
        return null; 
    }
}