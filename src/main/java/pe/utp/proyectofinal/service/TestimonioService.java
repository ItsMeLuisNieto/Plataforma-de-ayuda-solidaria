package pe.utp.proyectofinal.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.utp.proyectofinal.dto.TestimonioRequest;
import pe.utp.proyectofinal.entity.Testimonio;
import pe.utp.proyectofinal.entity.Usuario;
import pe.utp.proyectofinal.exception.BusinessException;
import pe.utp.proyectofinal.exception.NotFoundException;
import pe.utp.proyectofinal.repository.TestimonioRepository;
import pe.utp.proyectofinal.repository.UsuarioRepository;

@Service
public class TestimonioService {

    private final TestimonioRepository testimonioRepository;
    private final UsuarioRepository usuarioRepository;

    public TestimonioService(TestimonioRepository testimonioRepository,
                             UsuarioRepository usuarioRepository) {
        this.testimonioRepository = testimonioRepository;
        this.usuarioRepository = usuarioRepository;
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
                throw new BusinessException("invalid_request", "Los datos del testimonio son obligatorios");
            }
            return;
        }

        Usuario usuario = resolverUsuario(request, testimonio);
        if (usuario != null) {
            testimonio.setUsuario(usuario);
        } else if (esCreacion) {
            throw new BusinessException("usuario_required", "El usuarioId es obligatorio");
        }

        if (request.getComentario() != null && !request.getComentario().isBlank()) {
            testimonio.setComentario(request.getComentario().trim());
        } else if (esCreacion) {
            testimonio.setComentario("Gracias por todo el apoyo y la solidaridad.");
        }
    }

    private Usuario resolverUsuario(TestimonioRequest request, Testimonio testimonio) {
        if (request.getUsuarioId() != null) {
            return usuarioRepository.findById(request.getUsuarioId())
                    .orElseThrow(() -> new NotFoundException(
                            "Usuario no encontrado con id " + request.getUsuarioId()));
        }
        if (testimonio.getUsuario() != null) {
            return testimonio.getUsuario();
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName())) {
            return usuarioRepository.findByEmail(authentication.getName()).orElse(null);
        }
        return usuarioRepository.findAll().stream().findFirst().orElse(null);
    }
}
