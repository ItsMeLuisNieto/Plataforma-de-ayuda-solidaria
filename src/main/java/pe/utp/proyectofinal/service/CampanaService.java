package pe.utp.proyectofinal.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.utp.proyectofinal.dto.CampanaRequest;
import pe.utp.proyectofinal.entity.Campana;
import pe.utp.proyectofinal.entity.Usuario;
import pe.utp.proyectofinal.exception.BusinessException;
import pe.utp.proyectofinal.exception.NotFoundException;
import pe.utp.proyectofinal.repository.CampanaRepository;
import pe.utp.proyectofinal.repository.DonacionRepository;
import pe.utp.proyectofinal.repository.RecepcionDonacionRepository;
import pe.utp.proyectofinal.repository.UsuarioRepository;

@Service
public class CampanaService {

    private final CampanaRepository campanaRepository;
    private final DonacionRepository donacionRepository;
    private final RecepcionDonacionRepository recepcionDonacionRepository;
    private final UsuarioRepository usuarioRepository;

    public CampanaService(CampanaRepository campanaRepository,
                          DonacionRepository donacionRepository,
                          RecepcionDonacionRepository recepcionDonacionRepository,
                          UsuarioRepository usuarioRepository) {
        this.campanaRepository = campanaRepository;
        this.donacionRepository = donacionRepository;
        this.recepcionDonacionRepository = recepcionDonacionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Campana> listar() {
        return campanaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Campana> buscarFiltrado(String buscar, String nombre, String ubicacion) {
        List<Campana> lista = campanaRepository.findAll();

        if (buscar != null && !buscar.isBlank()) {
            String q = buscar.trim().toLowerCase();
            lista = lista.stream().filter(c ->
                (c.getNombre() != null && c.getNombre().toLowerCase().contains(q)) ||
                (c.getDescripcion() != null && c.getDescripcion().toLowerCase().contains(q)) ||
                (c.getUbicacion() != null && c.getUbicacion().toLowerCase().contains(q))
            ).collect(Collectors.toList());
        }

        if (nombre != null && !nombre.isBlank()) {
            String n = nombre.trim().toLowerCase();
            lista = lista.stream().filter(c ->
                c.getNombre() != null && c.getNombre().toLowerCase().contains(n)
            ).collect(Collectors.toList());
        }

        if (ubicacion != null && !ubicacion.isBlank()) {
            String u = ubicacion.trim().toLowerCase();
            lista = lista.stream().filter(c ->
                c.getUbicacion() != null && c.getUbicacion().toLowerCase().contains(u)
            ).collect(Collectors.toList());
        }

        return lista;
    }

    @Transactional(readOnly = true)
    public Campana buscar(Long id) {
        return obtenerEntidad(id);
    }

    @Transactional
    public Campana crear(CampanaRequest request) {
        Campana campana = new Campana();
        aplicarDatos(campana, request, true);
        if (campana.getUsuario() == null) {
            campana.setUsuario(resolverUsuarioActual());
        }
        return campanaRepository.save(campana);
    }

    @Transactional
    public Campana actualizar(Long id, CampanaRequest request) {
        Campana campana = obtenerEntidad(id);
        aplicarDatos(campana, request, false);
        return campanaRepository.save(campana);
    }

    @Transactional
    public void eliminar(Long id) {
        Campana campana = obtenerEntidad(id);
        // Limpiar registros relacionados para evitar fallos por llave foránea
        recepcionDonacionRepository.deleteByCampanaId(id);
        donacionRepository.deleteByCampanaId(id);
        campanaRepository.delete(campana);
    }

    private Campana obtenerEntidad(Long id) {
        return campanaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Campaña no encontrada con id " + id));
    }

    private Usuario resolverUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName())) {
            Usuario autenticado = usuarioRepository.findByEmail(authentication.getName()).orElse(null);
            if (autenticado != null) {
                return autenticado;
            }
        }
        return usuarioRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new BusinessException("usuario_required",
                        "No existe un usuario para asignar como creador de la campaña"));
    }

    private void aplicarDatos(Campana campana, CampanaRequest request, boolean esCreacion) {
        if (request == null) {
            if (esCreacion) {
                asignarValoresPorDefecto(campana);
            }
            return;
        }

        String nombreCampana = request.getNombre();

        if (nombreCampana != null && !nombreCampana.isBlank()) {
            campana.setNombre(nombreCampana.trim());
        } else if (esCreacion && (campana.getNombre() == null || campana.getNombre().isBlank())) {
            campana.setNombre("Campaña Solidaria");
        }

        if (request.getDescripcion() != null && !request.getDescripcion().isBlank()) {
            campana.setDescripcion(request.getDescripcion().trim());
        } else if (esCreacion && (campana.getDescripcion() == null || campana.getDescripcion().isBlank())) {
            campana.setDescripcion("Iniciativa de apoyo solidario para la comunidad.");
        }

        if (request.getMetaDias() != null) {
            campana.setMetaDias(request.getMetaDias());
        } else if (esCreacion && campana.getMetaDias() <= 0) {
            campana.setMetaDias(30);
        }

        if (request.getPorcentaje() != null) {
            campana.setPorcentaje(request.getPorcentaje());
        } else if (esCreacion) {
            campana.setPorcentaje(0);
        }

        if (request.getUbicacion() != null && !request.getUbicacion().isBlank()) {
            campana.setUbicacion(request.getUbicacion().trim());
        } else if (esCreacion && (campana.getUbicacion() == null || campana.getUbicacion().isBlank())) {
            campana.setUbicacion("Lima, Perú");
        }

        if (request.getBeneficiarios() != null) {
            campana.setBeneficiarios(request.getBeneficiarios());
        } else if (esCreacion && campana.getBeneficiarios() <= 0) {
            campana.setBeneficiarios(100);
        }

        if (request.getMeta() != null) {
            campana.setMeta(request.getMeta());
        } else if (esCreacion && campana.getMeta() <= 0) {
            campana.setMeta(1000);
        }
    }

    private void asignarValoresPorDefecto(Campana campana) {
        if (campana.getNombre() == null) campana.setNombre("Campaña Solidaria");
        if (campana.getDescripcion() == null) campana.setDescripcion("Iniciativa de apoyo solidario.");
        if (campana.getMetaDias() <= 0) campana.setMetaDias(30);
        if (campana.getUbicacion() == null) campana.setUbicacion("Lima, Perú");
        if (campana.getBeneficiarios() <= 0) campana.setBeneficiarios(100);
        if (campana.getMeta() <= 0) campana.setMeta(1000);
    }
}
