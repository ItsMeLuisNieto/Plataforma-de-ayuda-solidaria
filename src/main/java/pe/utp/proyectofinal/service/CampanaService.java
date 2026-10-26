package pe.utp.proyectofinal.service;

import java.util.List;
import java.util.Locale;

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
        campana.setTitulo(request.getTitulo().trim());
        campana.setDescripcion(request.getDescripcion().trim());
        campana.setMetaDias(normalizarDias(request.getMetaDias()));
        campana.setPorcentaje(request.getPorcentaje());
        campana.setUbicacion(request.getUbicacion().trim());
        campana.setBeneficiarios(request.getBeneficiarios().trim());
        campana.setMeta(normalizarMeta(request.getMeta()));
    }

    private String normalizarDias(String metaDias) {
        String valor = metaDias.trim();
        String valorMinusculas = valor.toLowerCase(Locale.ROOT);
        if (valorMinusculas.contains("día") || valorMinusculas.contains("dia")) {
            return valor;
        }
        return valor + " días";
    }

    private String normalizarMeta(String meta) {
        String valor = meta.replace("-", "").trim();
        if (!valor.toLowerCase(Locale.ROOT).startsWith("meta:")) {
            valor = "Meta: " + valor;
        }
        return valor;
    }
}
