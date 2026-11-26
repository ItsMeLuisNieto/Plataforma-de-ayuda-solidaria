package pe.utp.proyectofinal.service;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<Testimonio> buscarFiltrado(String buscar, String rol, String nombre) {
        List<Testimonio> lista = testimonioRepository.findAll();

        if (buscar != null && !buscar.isBlank()) {
            String q = buscar.trim().toLowerCase();
            lista = lista.stream().filter(t ->
                (t.getNombre() != null && t.getNombre().toLowerCase().contains(q)) ||
                (t.getRol() != null && t.getRol().toLowerCase().contains(q)) ||
                (t.getComentario() != null && t.getComentario().toLowerCase().contains(q))
            ).collect(Collectors.toList());
        }

        if (rol != null && !rol.isBlank()) {
            String r = rol.trim().toLowerCase();
            lista = lista.stream().filter(t ->
                t.getRol() != null && t.getRol().toLowerCase().contains(r)
            ).collect(Collectors.toList());
        }

        if (nombre != null && !nombre.isBlank()) {
            String n = nombre.trim().toLowerCase();
            lista = lista.stream().filter(t ->
                t.getNombre() != null && t.getNombre().toLowerCase().contains(n)
            ).collect(Collectors.toList());
        }

        return lista;
    }

    @Transactional(readOnly = true)
    public Testimonio buscar(Long id) {
        return obtenerEntidad(id);
    }

    @Transactional
    public Testimonio crear(TestimonioRequest request) {
        Testimonio testimonio = new Testimonio();
        aplicarDatos(testimonio, request, true);
        return testimonioRepository.save(testimonio);
    }

    @Transactional
    public Testimonio actualizar(Long id, TestimonioRequest request) {
        Testimonio testimonio = obtenerEntidad(id);
        aplicarDatos(testimonio, request, false);
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

    private void aplicarDatos(Testimonio testimonio, TestimonioRequest request, boolean esCreacion) {
        if (request == null) {
            if (esCreacion) {
                testimonio.setNombre("Anónimo");
                testimonio.setRol("Colaborador");
                testimonio.setComentario("Gracias por el apoyo brindado.");
            }
            return;
        }

        if (request.getNombre() != null && !request.getNombre().isBlank()) {
            testimonio.setNombre(request.getNombre().trim());
        } else if (esCreacion && (testimonio.getNombre() == null || testimonio.getNombre().isBlank())) {
            testimonio.setNombre("Anónimo");
        }

        if (request.getRol() != null && !request.getRol().isBlank()) {
            testimonio.setRol(request.getRol().trim());
        } else if (esCreacion && (testimonio.getRol() == null || testimonio.getRol().isBlank())) {
            testimonio.setRol("Colaborador");
        }

        if (request.getComentario() != null && !request.getComentario().isBlank()) {
            testimonio.setComentario(request.getComentario().trim());
        } else if (esCreacion && (testimonio.getComentario() == null || testimonio.getComentario().isBlank())) {
            testimonio.setComentario("Gracias por todo el apoyo y la solidaridad.");
        }
    }
}
