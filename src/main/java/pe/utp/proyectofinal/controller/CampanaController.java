package pe.utp.proyectofinal.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;
import pe.utp.proyectofinal.dto.CampanaRequest;
import pe.utp.proyectofinal.service.CampanaService;

@Controller
public class CampanaController {

    private final CampanaService campanaService;

    public CampanaController(CampanaService campanaService) {
        this.campanaService = campanaService;
    }

    @GetMapping("/campanas")
    public String verDonaciones(Model model, Authentication authentication) {
        model.addAttribute("campanas", campanaService.listar());
        model.addAttribute("esAdmin", esUsuarioAutenticado(authentication));
        return "campanas";
    }

    @PostMapping("/campanas")
    public String creacionCampana(@Valid @ModelAttribute CampanaRequest request, BindingResult result) {
        if (result.hasErrors()) {
            return "redirect:/campanas?error=invalid";
        }
        campanaService.crear(request);
        return "redirect:/campanas";
    }

    @PostMapping("/campanas/eliminar/{id}")
    public String eliminarCampana(@PathVariable Long id) {
        campanaService.eliminar(id);
        return "redirect:/campanas";
    }

    private boolean esUsuarioAutenticado(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName());
    }
}
