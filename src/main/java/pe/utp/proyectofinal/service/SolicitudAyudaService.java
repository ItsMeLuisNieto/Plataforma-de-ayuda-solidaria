package pe.utp.proyectofinal.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.utp.proyectofinal.dto.SolicitudAyudaRequest;
import pe.utp.proyectofinal.entity.SolicitudAyuda;
import pe.utp.proyectofinal.exception.NotFoundException;
import pe.utp.proyectofinal.repository.SolicitudAyudaRepository;

@Service
public class SolicitudAyudaService {

    private final SolicitudAyudaRepository solicitudAyudaRepository;

    public SolicitudAyudaService(SolicitudAyudaRepository solicitudAyudaRepository) {
        this.solicitudAyudaRepository = solicitudAyudaRepository;
    }

    @Transactional(readOnly = true)
    public List<SolicitudAyuda> listar() {
        return solicitudAyudaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public SolicitudAyuda buscar(Long id) {
        return obtenerEntidad(id);
    }

    @Transactional
    public SolicitudAyuda registrar(SolicitudAyudaRequest request) {
        SolicitudAyuda solicitud = new SolicitudAyuda();
        aplicarDatos(solicitud, request);
        solicitud.setEstado("Registrada");
        return solicitudAyudaRepository.save(solicitud);
    }

    @Transactional
    public SolicitudAyuda actualizar(Long id, SolicitudAyudaRequest request) {
        SolicitudAyuda solicitud = obtenerEntidad(id);
        aplicarDatos(solicitud, request);
        return solicitudAyudaRepository.save(solicitud);
    }

    @Transactional
    public void eliminar(Long id) {
        solicitudAyudaRepository.delete(obtenerEntidad(id));
    }

    private SolicitudAyuda obtenerEntidad(Long id) {
        return solicitudAyudaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Solicitud de ayuda no encontrada con id " + id));
    }

    private void aplicarDatos(SolicitudAyuda solicitud, SolicitudAyudaRequest request) {
        solicitud.setNombreDonantes(request.getNombreDonantes().trim());
        solicitud.setTipoAyuda(request.getTipoAyuda().trim());
        solicitud.setDescripcion(request.getDescripcion().trim());
    }
}
