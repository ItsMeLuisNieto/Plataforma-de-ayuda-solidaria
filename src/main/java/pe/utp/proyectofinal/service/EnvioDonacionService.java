package pe.utp.proyectofinal.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.utp.proyectofinal.exception.BusinessException;
import pe.utp.proyectofinal.exception.NotFoundException;
import pe.utp.proyectofinal.model.Donacion;
import pe.utp.proyectofinal.model.EnvioDonacion;
import pe.utp.proyectofinal.repository.EnvioDonacionRepository;

@Service
public class EnvioDonacionService {

    private final EnvioDonacionRepository envioDonacionRepository;

    public EnvioDonacionService(EnvioDonacionRepository envioDonacionRepository) {
        this.envioDonacionRepository = envioDonacionRepository;
    }

    @Transactional(readOnly = true)
    public List<EnvioDonacion> listar() {
        return envioDonacionRepository.findAllByOrderByFechaRegistroDesc();
    }

    @Transactional(readOnly = true)
    public EnvioDonacion buscar(Long id) {
        return envioDonacionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Envío de donación no encontrado con id " + id));
    }

    @Transactional
    public EnvioDonacion crearDesdeDonacion(Donacion donacion) {
        if (donacion == null || donacion.getCampana() == null) {
            return null;
        }
        EnvioDonacion envio = new EnvioDonacion();
        envio.setDonacion(donacion);
        envio.setCampana(donacion.getCampana());
        envio.setEstado("PENDIENTE");
        return envioDonacionRepository.save(envio);
    }

    @Transactional
    public EnvioDonacion enviar(Long id, Authentication authentication) {
        EnvioDonacion envio = buscar(id);
        if (!"PENDIENTE".equalsIgnoreCase(envio.getEstado())) {
            throw new BusinessException("Solo se pueden enviar registros en estado PENDIENTE");
        }
        envio.setEstado("ENVIADO");
        envio.setFechaEnvio(LocalDateTime.now());
        envio.setEnviadoPor(obtenerUsuario(authentication));
        return envioDonacionRepository.save(envio);
    }

    @Transactional
    public void eliminar(Long id) {
        EnvioDonacion envio = buscar(id);
        envioDonacionRepository.delete(envio);
    }

    private String obtenerUsuario(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return "Sistema";
        }
        return authentication.getName();
    }
}
