package pe.utp.proyectofinal.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.utp.proyectofinal.dto.DonacionRequest;
import pe.utp.proyectofinal.entity.Donacion;
import pe.utp.proyectofinal.exception.NotFoundException;
import pe.utp.proyectofinal.repository.DonacionRepository;

@Service
public class DonacionService {

    private final DonacionRepository donacionRepository;

    public DonacionService(DonacionRepository donacionRepository) {
        this.donacionRepository = donacionRepository;
    }

    @Transactional(readOnly = true)
    public List<Donacion> listar() {
        return donacionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Donacion buscar(Long id) {
        return obtenerEntidad(id);
    }

    @Transactional
    public Donacion registrar(DonacionRequest request) {
        Donacion donacion = new Donacion();
        aplicarDatos(donacion, request);
        donacion.setEstado(esBlanco(request.getEstado()) ? "Registrado" : request.getEstado().trim());
        return donacionRepository.save(donacion);
    }

    @Transactional
    public Donacion actualizar(Long id, DonacionRequest request) {
        Donacion donacion = obtenerEntidad(id);
        aplicarDatos(donacion, request);
        if (!esBlanco(request.getEstado())) {
            donacion.setEstado(request.getEstado().trim());
        }
        return donacionRepository.save(donacion);
    }

    @Transactional
    public void eliminar(Long id) {
        donacionRepository.delete(obtenerEntidad(id));
    }

    private Donacion obtenerEntidad(Long id) {
        return donacionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Donación no encontrada con id " + id));
    }

    private void aplicarDatos(Donacion donacion, DonacionRequest request) {
        donacion.setNombreDonante(request.getNombreDonante().trim());
        donacion.setProducto(request.getProducto().trim());
        donacion.setCantidad(request.getCantidad());
        if (!esBlanco(request.getCodigoLote())) {
            donacion.setCodigoLote(request.getCodigoLote().trim());
        }
    }

    private boolean esBlanco(String valor) {
        return valor == null || valor.isBlank();
    }
}
