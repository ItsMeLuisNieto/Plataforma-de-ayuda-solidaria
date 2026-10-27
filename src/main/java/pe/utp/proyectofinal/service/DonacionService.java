package pe.utp.proyectofinal.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.utp.proyectofinal.dto.DonacionRequest;
import pe.utp.proyectofinal.entity.Donacion;
import pe.utp.proyectofinal.entity.Usuario;
import pe.utp.proyectofinal.enums.MedioPago;
import pe.utp.proyectofinal.enums.TipoDonacion;
import pe.utp.proyectofinal.exception.NotFoundException;
import pe.utp.proyectofinal.repository.CampanaRepository;
import pe.utp.proyectofinal.repository.DonacionRepository;
import pe.utp.proyectofinal.repository.UsuarioRepository;

@Service
public class DonacionService {

    private final DonacionRepository donacionRepository;
    private final CampanaRepository campanaRepository;
    private final UsuarioRepository usuarioRepository;

    public DonacionService(DonacionRepository donacionRepository, 
                          CampanaRepository campanaRepository,
                          UsuarioRepository usuarioRepository) {
        this.donacionRepository = donacionRepository;
        this.campanaRepository = campanaRepository;
        this.usuarioRepository = usuarioRepository;
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
        donacion.setTipoDonacion(TipoDonacion.BIENES);
        donacion.setTelefono(request.getTelefono());
        donacion.setCorreo(request.getCorreo());
        return donacionRepository.save(donacion);
    }

    @Transactional
    public Donacion registrarDonacionYape(DonacionRequest request) {
        if (request.getNroOperacion() != null && !request.getNroOperacion().isBlank()) {
            if (donacionRepository.findByNroOperacion(request.getNroOperacion()).isPresent()) {
                throw new IllegalArgumentException("Ya existe una donación registrada con este número de operación.");
            }
        }

        Donacion donacion = new Donacion();
        donacion.setCodigoLote(request.getCodigoLote());
        donacion.setNombreDonante(request.getNombreDonante() != null ? request.getNombreDonante().trim() : null);
        donacion.setProducto("DONACIÓN MONETARIA - YAPE");
        donacion.setCantidad(1);
        donacion.setEstado("PENDIENTE_VERIFICACION");
        donacion.setTipoDonacion(TipoDonacion.MONETARIA);
        donacion.setMedioPago(MedioPago.YAPE);
        donacion.setMonto(request.getMonto());
        donacion.setNroOperacion(request.getNroOperacion());
        donacion.setTelefono(request.getTelefono());
        donacion.setCorreo(request.getCorreo());
        donacion.setFechaTransferencia(request.getFechaTransferencia());

        if (request.getCampanaId() != null) {
            campanaRepository.findById(request.getCampanaId()).ifPresent(donacion::setCampana);
        }

        return donacionRepository.save(donacion);
    }

    @Transactional
    public Donacion confirmarDonacion(Long id) {
        Donacion donacion = obtenerEntidad(id);
        
        if (!"PENDIENTE_VERIFICACION".equals(donacion.getEstado())) {
            throw new IllegalArgumentException("Solo se pueden confirmar donaciones en estado PENDIENTE_VERIFICACION");
        }
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario verificador = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuario verificador no encontrado"));
        
        donacion.setEstado("CONFIRMADO");
        donacion.setVerificadoPor(verificador);
        donacion.setFechaVerificacion(LocalDateTime.now());
        donacion.setMotivoRechazo(null);
        
        return donacionRepository.save(donacion);
    }

    @Transactional
    public Donacion rechazarDonacion(Long id, String motivoRechazo) {
        Donacion donacion = obtenerEntidad(id);
        
        if (!"PENDIENTE_VERIFICACION".equals(donacion.getEstado())) {
            throw new IllegalArgumentException("Solo se pueden rechazar donaciones en estado PENDIENTE_VERIFICACION");
        }
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario verificador = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuario verificador no encontrado"));
        
        donacion.setEstado("RECHAZADO");
        donacion.setVerificadoPor(verificador);
        donacion.setFechaVerificacion(LocalDateTime.now());
        donacion.setMotivoRechazo(motivoRechazo);
        
        return donacionRepository.save(donacion);
    }

    @Transactional
    public Donacion actualizar(Long id, DonacionRequest request) {
        Donacion donacion = obtenerEntidad(id);
        aplicarDatos(donacion, request);
        if (!esBlanco(request.getEstado())) {
            donacion.setEstado(request.getEstado().trim());
        }
        donacion.setTelefono(request.getTelefono());
        donacion.setCorreo(request.getCorreo());
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
        if (!esBlanco(request.getNombreDonante())) {
            donacion.setNombreDonante(request.getNombreDonante().trim());
        }
        if (!esBlanco(request.getProducto())) {
            donacion.setProducto(request.getProducto().trim());
        }
        if (request.getCantidad() > 0) {
            donacion.setCantidad(request.getCantidad());
        }
        if (!esBlanco(request.getCodigoLote())) {
            donacion.setCodigoLote(request.getCodigoLote().trim());
        }
    }

    private boolean esBlanco(String valor) {
        return valor == null || valor.isBlank();
    }
}
