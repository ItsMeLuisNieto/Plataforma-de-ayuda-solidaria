package pe.utp.proyectofinal.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.utp.proyectofinal.dto.DonacionRequest;
import pe.utp.proyectofinal.entity.Campana;
import pe.utp.proyectofinal.entity.Donacion;
import pe.utp.proyectofinal.exception.NotFoundException;
import pe.utp.proyectofinal.repository.CampanaRepository;
import pe.utp.proyectofinal.repository.DonacionRepository;
import pe.utp.proyectofinal.repository.RecepcionDonacionRepository;

@Service
public class DonacionService {

    private final DonacionRepository donacionRepository;
    private final CampanaRepository campanaRepository;
    private final RecepcionDonacionService recepcionDonacionService;
    private final RecepcionDonacionRepository recepcionDonacionRepository;

    public DonacionService(DonacionRepository donacionRepository,
                           CampanaRepository campanaRepository,
                           RecepcionDonacionService recepcionDonacionService,
                           RecepcionDonacionRepository recepcionDonacionRepository) {
        this.donacionRepository = donacionRepository;
        this.campanaRepository = campanaRepository;
        this.recepcionDonacionService = recepcionDonacionService;
        this.recepcionDonacionRepository = recepcionDonacionRepository;
    }

    @Transactional(readOnly = true)
    public List<Donacion> listar() {
        return donacionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Donacion> buscarFiltrado(Long campanaId, String estado, String donante, String producto, String buscar) {
        List<Donacion> lista = donacionRepository.findAll();

        if (campanaId != null) {
            lista = lista.stream()
                    .filter(d -> d.getCampana() != null && campanaId.equals(d.getCampana().getId()))
                    .collect(Collectors.toList());
        }

        if (estado != null && !estado.isBlank()) {
            String est = estado.trim().toLowerCase();
            lista = lista.stream()
                    .filter(d -> d.getEstado() != null && d.getEstado().toLowerCase().contains(est))
                    .collect(Collectors.toList());
        }

        if (donante != null && !donante.isBlank()) {
            String don = donante.trim().toLowerCase();
            lista = lista.stream()
                    .filter(d -> d.getNombreDonante() != null && d.getNombreDonante().toLowerCase().contains(don))
                    .collect(Collectors.toList());
        }

        if (producto != null && !producto.isBlank()) {
            String prod = producto.trim().toLowerCase();
            lista = lista.stream()
                    .filter(d -> d.getProducto() != null && d.getProducto().toLowerCase().contains(prod))
                    .collect(Collectors.toList());
        }

        if (buscar != null && !buscar.isBlank()) {
            String q = buscar.trim().toLowerCase();
            lista = lista.stream()
                    .filter(d ->
                        (d.getNombreDonante() != null && d.getNombreDonante().toLowerCase().contains(q)) ||
                        (d.getProducto() != null && d.getProducto().toLowerCase().contains(q)) ||
                        (d.getCodigoLote() != null && d.getCodigoLote().toLowerCase().contains(q)) ||
                        (d.getEstado() != null && d.getEstado().toLowerCase().contains(q))
                    )
                    .collect(Collectors.toList());
        }

        return lista;
    }

    @Transactional(readOnly = true)
    public Donacion buscar(Long id) {
        return obtenerEntidad(id);
    }

    @Transactional
    public Donacion registrar(DonacionRequest request) {
        Donacion donacion = new Donacion();

        // Obtener o asignar campaña de forma flexible
        Campana campana = resolverCampana(request != null ? request.getCampanaId() : null);
        donacion.setCampana(campana);

        // Asignar datos con defaults inteligentes
        aplicarDatos(donacion, request, true);

        Donacion guardada = donacionRepository.save(donacion);
        try {
            recepcionDonacionService.crearDesdeDonacion(guardada);
        } catch (Exception ignored) {
            // No bloquear registro si el módulo de recepción tiene alguna incidencia
        }
        return guardada;
    }

    @Transactional
    public Donacion actualizar(Long id, DonacionRequest request) {
        Donacion donacion = obtenerEntidad(id);

        if (request != null && request.getCampanaId() != null) {
            Campana campana = campanaRepository.findById(request.getCampanaId()).orElse(null);
            if (campana != null) {
                donacion.setCampana(campana);
            }
        }

        aplicarDatos(donacion, request, false);
        return donacionRepository.save(donacion);
    }

    @Transactional
    public void eliminar(Long id) {
        Donacion donacion = obtenerEntidad(id);
        // Limpiar recepciones asociadas primero para evitar error de clave foránea
        recepcionDonacionRepository.deleteByDonacionId(id);
        donacionRepository.delete(donacion);
    }

    private Donacion obtenerEntidad(Long id) {
        return donacionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Donación no encontrada con id " + id));
    }

    private Campana resolverCampana(Long campanaId) {
        if (campanaId != null) {
            return campanaRepository.findById(campanaId).orElseGet(this::obtenerOCrearCampanaPorDefecto);
        }
        return obtenerOCrearCampanaPorDefecto();
    }

    private Campana obtenerOCrearCampanaPorDefecto() {
        return campanaRepository.findAll().stream().findFirst().orElseGet(() -> {
            Campana def = new Campana();
            def.setNombre("Campaña General de Ayuda");
            def.setDescripcion("Campaña predeterminada para donaciones solidarias");
            def.setMetaDias(30);
            def.setPorcentaje(0);
            def.setUbicacion("Lima, Perú");
            def.setBeneficiarios(100);
            def.setMeta(1000);
            return campanaRepository.save(def);
        });
    }

    private String generarCodigoLote() {
        return "LOTE-" + (System.currentTimeMillis() % 1000000) + "-" + java.util.UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    private void aplicarDatos(Donacion donacion, DonacionRequest request, boolean esCreacion) {
        if (request == null) {
            if (esCreacion) {
                donacion.setNombreDonante("Donante Anónimo");
                donacion.setProducto("Víveres y bienes generales");
                donacion.setCantidad(1);
                donacion.setEstado("PENDIENTE");
                donacion.setCodigoLote(generarCodigoLote());
            }
            return;
        }

        if (request.getNombreDonante() != null && !request.getNombreDonante().isBlank()) {
            donacion.setNombreDonante(request.getNombreDonante().trim());
        } else if (esCreacion && (donacion.getNombreDonante() == null || donacion.getNombreDonante().isBlank())) {
            donacion.setNombreDonante("Donante Anónimo");
        }

        if (request.getProducto() != null && !request.getProducto().isBlank()) {
            donacion.setProducto(request.getProducto().trim());
        } else if (esCreacion && (donacion.getProducto() == null || donacion.getProducto().isBlank())) {
            donacion.setProducto("Víveres y bienes generales");
        }

        if (request.getCantidad() != null && request.getCantidad() > 0) {
            donacion.setCantidad(request.getCantidad());
        } else if (esCreacion && (donacion.getCantidad() == null || donacion.getCantidad() <= 0)) {
            donacion.setCantidad(1);
        }

        if (request.getEstado() != null && !request.getEstado().isBlank()) {
            donacion.setEstado(request.getEstado().trim().toUpperCase());
        } else if (esCreacion && (donacion.getEstado() == null || donacion.getEstado().isBlank())) {
            donacion.setEstado("PENDIENTE");
        }

        if (request.getCodigoLote() != null && !request.getCodigoLote().isBlank()) {
            donacion.setCodigoLote(request.getCodigoLote().trim());
        } else if (esCreacion && (donacion.getCodigoLote() == null || donacion.getCodigoLote().isBlank())) {
            donacion.setCodigoLote(generarCodigoLote());
        }
    }
}
