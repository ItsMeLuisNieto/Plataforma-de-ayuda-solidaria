package pe.utp.proyectofinal.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.utp.proyectofinal.dto.CampanaRequest;
import pe.utp.proyectofinal.entity.Campana;
import pe.utp.proyectofinal.exception.NotFoundException;
import pe.utp.proyectofinal.repository.CampanaRepository;

@Service
public class CampanaService {

    private final CampanaRepository campanaRepository;

    public CampanaService(CampanaRepository campanaRepository) {
        this.campanaRepository = campanaRepository;
    }

    @Transactional(readOnly = true)
    public List<Campana> listar() {
        return campanaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Campana buscar(Long id) {
        return obtenerEntidad(id);
    }

    @Transactional
    public Campana crear(CampanaRequest request) {
        Campana campana = new Campana();
        aplicarDatos(campana, request);
        return campanaRepository.save(campana);
    }

    @Transactional
    public Campana actualizar(Long id, CampanaRequest request) {
        Campana campana = obtenerEntidad(id);
        aplicarDatos(campana, request);
        return campanaRepository.save(campana);
    }

    @Transactional
    public void eliminar(Long id) {
        campanaRepository.delete(obtenerEntidad(id));
    }

    private Campana obtenerEntidad(Long id) {
        return campanaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Campaña no encontrada con id " + id));
    }

    private void aplicarDatos(Campana campana, CampanaRequest request) {
        String nombreCampana = request.getNombre() != null ? request.getNombre() : request.getTitulo();
        campana.setNombre(nombreCampana != null ? nombreCampana.trim() : null);
        if (request.getDescripcion() != null) {
            campana.setDescripcion(request.getDescripcion().trim());
        }
        if (request.getMetaDias() != null) {
            campana.setMetaDias(request.getMetaDias());
        }
        if (request.getPorcentaje() != null) {
            campana.setPorcentaje(request.getPorcentaje());
        }
        if (request.getUbicacion() != null) {
            campana.setUbicacion(request.getUbicacion().trim());
        }
        if (request.getBeneficiarios() != null) {
            campana.setBeneficiarios(request.getBeneficiarios());
        }
        if (request.getMeta() != null) {
            campana.setMeta(request.getMeta());
        }
    }
}
