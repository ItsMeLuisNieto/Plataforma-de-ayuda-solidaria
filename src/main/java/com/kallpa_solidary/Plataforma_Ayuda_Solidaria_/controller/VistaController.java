package com.kallpa_solidary.Plataforma_Ayuda_Solidaria_.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VistaController {

    // 1. Página de inicio 
    @GetMapping("/kallpa-solidary")
    public String inicio() {
        return "index"; // Carga src/main/resources/templates/index.html
    }

    // 2. Sobre Nosotros
    @GetMapping("/sobre-nosotros")
    public String sobreNosotros() {
        return "about"; // Carga src/main/resources/templates/about.html
    }

    // 3. Login
    @GetMapping("/login")
    public String irLogin() {
        return "login"; // Carga src/main/resources/templates/login.html
    }

    // 4. Registro
    @GetMapping("/registro")
    public String irRegistro() {
        return "registro"; // Carga src/main/resources/templates/registro.html
    }

    // 5. Redirecciones hacia el controlador dinámico de Campañas (CampanaController)
    @GetMapping({"/donar-ahora", "/ver-voluntario", "/donar-bienes"})
    public String redirigirDonar() {
        return "redirect:/campanas"; // Lleva al método que inyecta la lista dinámica
    }

    @GetMapping("/eventos")
    public String irEventos() {
        return "redirect:/campanas#events";
    }

    @GetMapping("/ver-campanas")
    public String verCampanas() {
        return "redirect:/campanas#campaigns";
    }
}
