package pe.utp.proyectofinal.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pe.utp.proyectofinal.dto.TestimonioRequest;
import pe.utp.proyectofinal.entity.Testimonio;
import pe.utp.proyectofinal.service.TestimonioService;

@RestController
@RequestMapping("/api/testimonios")
public class TestimoniosController {

    private final TestimonioService testimonioService;

    public TestimoniosController(TestimonioService testimonioService) {
        this.testimonioService = testimonioService;
    }

    @GetMapping
    public List<Testimonio> obtenerTestimonios() {
        return testimonioService.listar();
    }

    @GetMapping("/{id}")
    public Testimonio obtenerTestimonio(@PathVariable Long id) {
        return testimonioService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Testimonio agregarTestimonio(@Valid @RequestBody TestimonioRequest request) {
        return testimonioService.crear(request);
    }

    @PutMapping("/{id}")
    public Testimonio actualizarTestimonio(@PathVariable Long id,
                                                   @Valid @RequestBody TestimonioRequest request) {
        return testimonioService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarTestimonio(@PathVariable Long id) {
        testimonioService.eliminar(id);
    }
}
