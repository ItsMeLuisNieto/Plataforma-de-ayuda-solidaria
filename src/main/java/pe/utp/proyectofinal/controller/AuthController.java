package pe.utp.proyectofinal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pe.utp.proyectofinal.model.Usuario;
import pe.utp.proyectofinal.repository.UsuarioRepository;

@Controller
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/registrar")
    public String registrarUsuario(@RequestParam String email, @RequestParam String pass, @RequestParam String nombre, @RequestParam String confirm_pass) {
        // 1. Verificamos si el correo ya existe
        if (usuarioRepository.existsByEmail(email)) {
            return "redirect:/registro?error=email_exists";
        }

        if (nombre.length() < 3) {
            return "redirect:/registro?error=invalid_name";
        }

        if (email == null || email == "") {
            return "redirect:/registro?error=null_email";
        }

        if (!pass.equals(confirm_pass) || pass == null) {
            return "redirect:/registro?error=pass_mismatch";
        }

        // 2. Creamos la entidad
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setEmail(email);

        // 3. Asignamos nombre de usuario
        nuevoUsuario.setNombre(nombre);
        
        // 4. Encriptamos la contraseña y asignamos
        nuevoUsuario.setPassword(passwordEncoder.encode(pass)); 
        
        // 5. Guardamos en Neon
        usuarioRepository.save(nuevoUsuario);
        
        // 5. Redirigimos al login tras el éxito
        return "redirect:/login";
    }
}