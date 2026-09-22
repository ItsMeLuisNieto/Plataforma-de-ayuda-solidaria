package com.kallpa_solidary.Plataforma_Ayuda_Solidaria_.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.kallpa_solidary.Plataforma_Ayuda_Solidaria_.dto.CampDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CampanaController {
    private static List<CampDTO> listaCampanas = new ArrayList<>();

    static {
        listaCampanas.add(new CampDTO(1L, "Canasta de alimentos", "Aportamos kits básicos para familias vulnerables.",
                "7 días", 12, "San Martín de Porres", "240 familias", "Meta: S/ 18,000"));
        listaCampanas.add(new CampDTO(2L, "Ropa y abrigo", "Recibimos prendas, mochilas y calzado para niños.",
                "14 días", 58, "El Agustino", "180 beneficiarios", "Meta: 1,300 prendas"));
        listaCampanas.add(new CampDTO(3L, "Material escolar", "Apoyamos a estudiantes con cuadernos y uniformes.",
                "21 días", 81, "Surco", "310 estudiantes", "Meta: S/ 24,500"));
    }

    // Ruta para renderizar donate.html con Thymeleaf
    @GetMapping("/campanas")
    public String verDonaciones(Model model) {
        model.addAttribute("campanas", listaCampanas);
        model.addAttribute("esAdmin", true);
        return "donate";
    }

    @PostMapping("/campanas")
    public String CreacionCampana(@ModelAttribute CampDTO nuevaCampana) {
        // Si viene solo el número (ej: "15"), le añadimos " días"
        if (!nuevaCampana.getMetaDias().toLowerCase().contains("días")) {
            nuevaCampana.setMetaDias(nuevaCampana.getMetaDias().trim() + " días");
        }
        // Si contiene un signo negativo, se elimina o se reemplaza
        if (nuevaCampana.getMeta().contains("-")) {
            nuevaCampana.setMeta(nuevaCampana.getMeta().replace("-", ""));
        }

        // Si no incluye el prefijo "Meta:", se le agrega automáticamente
        if (!nuevaCampana.getMeta().startsWith("Meta:")) {
            nuevaCampana.setMeta("Meta: " + nuevaCampana.getMeta().trim());
        }
        long nuevoId = listaCampanas.size() + 1L;
        nuevaCampana.setId(nuevoId);
        listaCampanas.add(nuevaCampana);
        return "redirect:/campanas"; // Redirige a la página de campañas después de agregar una nueva

    }

    @PostMapping("/campanas/eliminar/{id}")
    public String eliminarCampana(@PathVariable Long id) {
        listaCampanas.removeIf(campana -> campana.getId().equals(id));
        return "redirect:/campanas";
    }

}