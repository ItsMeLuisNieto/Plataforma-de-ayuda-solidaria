package pe.utp.proyectofinal.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.utp.proyectofinal.dto.TestimonioRequest;
import pe.utp.proyectofinal.model.Testimonio;
import pe.utp.proyectofinal.model.Usuario;
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
        if (request == null) {
            throw new BusinessException("invalid_request", "Los datos del testimonio son obligatorios");
        }

        Testimonio testimonio = new Testimonio();
        Usuario actual = usuarioAutenticado();

        if (actual != null && !esAdmin(actual)) {
            // Un usuario normal solo puede publicar testimonios en su propio nombre
            testimonio.setUsuario(actual);
        } else {
            // Admin (o contexto sin autenticar): respeta el usuarioId de la petición
            Usuario objetivo = null;
            if (request.getUsuarioId() != null) {
                objetivo = obtenerUsuario(request.getUsuarioId());
            } else if (actual != null) {
                objetivo = actual;
            }
            if (objetivo == null) {
                throw new BusinessException("usuario_required", "El usuarioId es obligatorio");
            }
            testimonio.setUsuario(objetivo);
        }

        aplicarComentario(testimonio, request);
        return testimonioRepository.save(testimonio);
    }

    @Transactional
    public Testimonio actualizar(Long id, TestimonioRequest request) {
        Testimonio testimonio = obtenerEntidad(id);
        Usuario actual = usuarioAutenticado();

        if (actual != null && !esAdmin(actual)
                && (testimonio.getUsuario() == null || !actual.getId().equals(testimonio.getUsuario().getId()))) {
            throw new AccessDeniedException("Solo puedes editar tus propios testimonios");
        }

        // Solo el admin puede reasignar el propietario del testimonio
        if (request != null && esAdmin(actual) && request.getUsuarioId() != null) {
            testimonio.setUsuario(obtenerUsuario(request.getUsuarioId()));
        }

        aplicarComentario(testimonio, request);
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

    private Usuario obtenerUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con id " + id));
    }

    private void aplicarComentario(Testimonio testimonio, TestimonioRequest request) {
        if (request != null && request.getComentario() != null && !request.getComentario().isBlank()) {
            testimonio.setComentario(request.getComentario().trim());
        } else if (testimonio.getComentario() == null) {
            testimonio.setComentario("Gracias por todo el apoyo y la solidaridad.");
        }
    }

    private Usuario usuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            return null;
        }
        return usuarioRepository.findByEmail(authentication.getName()).orElse(null);
    }

    private boolean esAdmin(Usuario usuario) {
        return usuario != null && "ADMIN".equalsIgnoreCase(usuario.getRol());
    }
}
