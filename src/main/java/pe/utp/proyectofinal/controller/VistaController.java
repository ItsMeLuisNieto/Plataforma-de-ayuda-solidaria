package pe.utp.proyectofinal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import pe.utp.proyectofinal.service.ReporteService;

@Controller
public class VistaController {

    private final ReporteService reporteService;

    public VistaController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/kallpa-solidary")
    public String inicio() {
        return "index";
    }

    @GetMapping("/sobre-nosotros")
    public String sobreNosotros() {
        return "about";
    }

    @GetMapping("/reportes")
    public String reportes(Model model) {
        model.addAttribute("kpis", reporteService.obtenerKpis());
        model.addAttribute("movimientos", reporteService.obtenerMovimientos());
        return "reportes";
    }

    @GetMapping("/login")
    public String irLogin() {
        return "login";
    }

    @GetMapping("/registro")
    public String irRegistro() {
        return "registro";
    }

    @GetMapping({"/donar-ahora", "/ver-voluntario", "/donar-bienes"})
    public String redirigirDonar() {
        return "redirect:/campanas";
    }

    @GetMapping("/eventos")
    public String irEventos() {
        return "redirect:/campanas#events";
    }

    @GetMapping("/ver-campanas")
    public String verCampanas() {
        return "redirect:/campanas#campaigns";
    }

    @GetMapping("/solicitudes")
    public String solicitudes() {
        return "solicitudes";
    }

    @GetMapping("/donaciones")
    public String irDonaciones() {
        return "donaciones";
    }
}
