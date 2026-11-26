package pe.utp.proyectofinal.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.utp.proyectofinal.entity.Donacion;
import pe.utp.proyectofinal.entity.RecepcionDonacion;
import pe.utp.proyectofinal.exception.NotFoundException;
import pe.utp.proyectofinal.repository.DonacionRepository;
import pe.utp.proyectofinal.repository.RecepcionDonacionRepository;

@Service
public class RecepcionDonacionService {

    private final RecepcionDonacionRepository recepcionDonacionRepository;
    private final DonacionRepository donacionRepository;

    public RecepcionDonacionService(RecepcionDonacionRepository recepcionDonacionRepository,
                                    DonacionRepository donacionRepository) {
        this.recepcionDonacionRepository = recepcionDonacionRepository;
        this.donacionRepository = donacionRepository;
    }

    @Transactional(readOnly = true)
    public List<RecepcionDonacion> listar() {
        return recepcionDonacionRepository.findAllByOrderByFechaSolicitudDesc();
    }

    @Transactional(readOnly = true)
    public List<RecepcionDonacion> listarPendientes() {
        return recepcionDonacionRepository.findByEstadoOrderByFechaSolicitudAsc("PENDIENTE");
    }

    @Transactional
    public RecepcionDonacion confirmar(Long id, Authentication authentication) {
        RecepcionDonacion recepcion = obtenerEntidad(id);
        if (!"PENDIENTE".equalsIgnoreCase(recepcion.getEstado())) {
            throw new IllegalStateException("Solo se pueden confirmar donaciones en estado PENDIENTE");
        }
        recepcion.setEstado("CONFIRMADO");
        recepcion.setFechaRevision(LocalDateTime.now());
        recepcion.setRevisadoPor(obtenerUsuario(authentication));
        recepcion.setMotivoRechazo(null);

        Donacion donacion = recepcion.getDonacion();
        if (donacion != null) {
            donacion.setEstado("CONFIRMADO");
            donacionRepository.save(donacion);
        }

        return recepcionDonacionRepository.save(recepcion);
    }

    @Transactional
    public RecepcionDonacion rechazar(Long id, String motivo, Authentication authentication) {
        RecepcionDonacion recepcion = obtenerEntidad(id);
        if (!"PENDIENTE".equalsIgnoreCase(recepcion.getEstado())) {
            throw new IllegalStateException("Solo se pueden rechazar donaciones en estado PENDIENTE");
        }
        recepcion.setEstado("RECHAZADO");
        recepcion.setFechaRevision(LocalDateTime.now());
        recepcion.setRevisadoPor(obtenerUsuario(authentication));
        recepcion.setMotivoRechazo(motivo != null && !motivo.isBlank() ? motivo.trim() : "Sin motivo especificado");

        Donacion donacion = recepcion.getDonacion();
        if (donacion != null) {
            donacion.setEstado("RECHAZADO");
            donacionRepository.save(donacion);
        }

        return recepcionDonacionRepository.save(recepcion);
    }

    @Transactional
    public RecepcionDonacion crearDesdeDonacion(Donacion donacion) {
        if (donacion == null || donacion.getCampana() == null) {
            return null;
        }
        RecepcionDonacion recepcion = new RecepcionDonacion();
        recepcion.setDonacion(donacion);
        recepcion.setCampana(donacion.getCampana());
        recepcion.setEstado("PENDIENTE");
        return recepcionDonacionRepository.save(recepcion);
    }

    private RecepcionDonacion obtenerEntidad(Long id) {
        return recepcionDonacionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Recepción de donación no encontrada con id " + id));
    }

    private String obtenerUsuario(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return "Sistema";
        }
        return authentication.getName();
    }
}
