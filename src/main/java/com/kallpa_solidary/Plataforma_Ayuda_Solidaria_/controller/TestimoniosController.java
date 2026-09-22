package com.kallpa_solidary.Plataforma_Ayuda_Solidaria_.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kallpa_solidary.Plataforma_Ayuda_Solidaria_.dto.TestimoniosDto;

@RestController
@RequestMapping("/api/testimonios")
@CrossOrigin(origins = "*")
public class TestimoniosController {
    private static List<TestimoniosDto> listaTestimonio = new ArrayList<>();
    static {
        listaTestimonio.add(new TestimoniosDto(1L, "Luna Torres", "Donante",
                "Increíble la transparencia con la que entregan la ayuda humanitaria en cada campaña."));
        listaTestimonio.add(new TestimoniosDto(2L, "Enrique Lee", "Voluntario",
                "Ser parte de los voluntariados me ha permitido ver el impacto real en las comunidades."));
    }

    @GetMapping
    public List<TestimoniosDto> obtenerTestimonio() {
        return listaTestimonio;
    }

    @PostMapping
    public TestimoniosDto agregarTestimonio(@RequestBody TestimoniosDto nuevoTestimonio) {
        nuevoTestimonio.setId((long) (listaTestimonio.size() + 1));
        listaTestimonio.add(nuevoTestimonio);
        return nuevoTestimonio;

    }

    @PutMapping("/{id}")
    public TestimoniosDto actualizarTestimonio(@PathVariable Long id, @RequestBody TestimoniosDto testimonioActualizado) {
        for (TestimoniosDto t : listaTestimonio) {
            if (t.getId().equals(id)) {
                t.setNombre(testimonioActualizado.getNombre());
                t.setRol(testimonioActualizado.getRol());
                t.setComentario(testimonioActualizado.getComentario());
                return t; 
            }
        }
        return null; 
    }
}
