package pe.utp.proyectofinal.controller;

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

import pe.utp.proyectofinal.dto.TestimoniosDTO;

@RestController
@RequestMapping("/api/testimonios")
@CrossOrigin(origins = "*")
public class TestimoniosController {
    private static List<TestimoniosDTO> listaTestimonio = new ArrayList<>();
    static {
        listaTestimonio.add(new TestimoniosDTO(1L, "Luna Torres", "Donante",
                "Increíble la transparencia con la que entregan la ayuda humanitaria en cada campaña."));
        listaTestimonio.add(new TestimoniosDTO(2L, "Enrique Lee", "Voluntario",
                "Ser parte de los voluntariados me ha permitido ver el impacto real en las comunidades."));
    }

    @GetMapping
    public List<TestimoniosDTO> obtenerTestimonio() {
        return listaTestimonio;
    }

    @PostMapping
    public TestimoniosDTO agregarTestimonio(@RequestBody TestimoniosDTO nuevoTestimonio) {
        nuevoTestimonio.setId((long) (listaTestimonio.size() + 1));
        listaTestimonio.add(nuevoTestimonio);
        return nuevoTestimonio;

    }

    @PutMapping("/{id}")
    public TestimoniosDTO actualizarTestimonio(@PathVariable Long id, @RequestBody TestimoniosDTO testimonioActualizado) {
        for (TestimoniosDTO t : listaTestimonio) {
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
