package pe.utp.proyectofinal.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.utp.proyectofinal.dto.TestimonioRequest;
import pe.utp.proyectofinal.entity.Testimonio;
import pe.utp.proyectofinal.exception.NotFoundException;
import pe.utp.proyectofinal.repository.TestimonioRepository;

@Service
public class TestimonioService {

    private final TestimonioRepository testimonioRepository;

    public TestimonioService(TestimonioRepository testimonioRepository) {
        this.testimonioRepository = testimonioRepository;
    }

    @Transactional(readOnly = true)
    public List<Testimonio> listar() {
        return testimonioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Testimonio buscar(Long id) {
        return obtenerEntidad(id);
    }

    @Transactional
    public Testimonio crear(TestimonioRequest request) {
        Testimonio testimonio = new Testimonio();
        aplicarDatos(testimonio, request);
        return testimonioRepository.save(testimonio);
    }

    @Transactional
    public Testimonio actualizar(Long id, TestimonioRequest request) {
        Testimonio testimonio = obtenerEntidad(id);
        aplicarDatos(testimonio, request);
        return testimonioRepository.save(testimonio);
    }

    @Transactional
    public void eliminar(Long id) {
        testimonioRepository.delete(obtenerEntidad(id));
    }

    private Testimonio obtenerEntidad(Long id) {
        return testimonioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Testimonio no encontrado con id " + id));
    }

    private void aplicarDatos(Testimonio testimonio, TestimonioRequest request) {
        testimonio.setNombre(request.getNombre().trim());
        testimonio.setRol(request.getRol().trim());
        testimonio.setComentario(request.getComentario().trim());
    }
}
