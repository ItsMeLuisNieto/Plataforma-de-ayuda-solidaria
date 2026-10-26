package pe.utp.proyectofinal.service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.utp.proyectofinal.dto.ReporteDTO;
import pe.utp.proyectofinal.entity.Campana;
import pe.utp.proyectofinal.entity.Donacion;
import pe.utp.proyectofinal.entity.SolicitudAyuda;
import pe.utp.proyectofinal.repository.CampanaRepository;
import pe.utp.proyectofinal.repository.DonacionRepository;
import pe.utp.proyectofinal.repository.SolicitudAyudaRepository;

@Service
public class ReporteService {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final CampanaRepository campanaRepository;
    private final DonacionRepository donacionRepository;
    private final SolicitudAyudaRepository solicitudAyudaRepository;

    public ReporteService(CampanaRepository campanaRepository,
                          DonacionRepository donacionRepository,
                          SolicitudAyudaRepository solicitudAyudaRepository) {
        this.campanaRepository = campanaRepository;
        this.donacionRepository = donacionRepository;
        this.solicitudAyudaRepository = solicitudAyudaRepository;
    }

    @Transactional(readOnly = true)
    public ReporteDTO obtenerKpis() {
        List<Campana> campanas = campanaRepository.findAll();
        ReporteDTO kpis = new ReporteDTO();
        kpis.setTotalMontoRecaudado(BigDecimal.ZERO);
        kpis.setTotalCampanasActivas(campanas.size());
        kpis.setTotalBeneficiariosAtendidos(campanas.stream()
                .mapToInt(campana -> extraerEntero(campana.getBeneficiarios()))
                .sum());
        kpis.setTotalBienesStock(donacionRepository.findAll().stream()
                .mapToInt(Donacion::getCantidad)
                .sum());
        return kpis;
    }

    @Transactional(readOnly = true)
    public List<ReporteDTO> obtenerMovimientos() {
        List<ReporteDTO> movimientos = new ArrayList<>();

        donacionRepository.findAll().stream()
                .map(this::mapearDonacion)
                .forEach(movimientos::add);

        solicitudAyudaRepository.findAll().stream()
                .map(this::mapearSolicitud)
                .forEach(movimientos::add);

        movimientos.sort(Comparator.comparing(ReporteDTO::getFecha).reversed());
        return movimientos;
    }

    private ReporteDTO mapearDonacion(Donacion donacion) {
        ReporteDTO dto = new ReporteDTO();
        dto.setIdTransaccion(donacion.getId());
        dto.setFecha(donacion.getFechaIngreso() == null ? "" : donacion.getFechaIngreso().format(FORMATO_FECHA));
        dto.setCampanaTitulo(donacion.getProducto());
        dto.setDistrito("-");
        dto.setTipoDonacion("Bienes");
        dto.setValorMonto(donacion.getCantidad() + (donacion.getCantidad() == 1 ? " unidad" : " unidades"));
        dto.setEstado(donacion.getEstado());
        return dto;
    }

    private ReporteDTO mapearSolicitud(SolicitudAyuda solicitud) {
        ReporteDTO dto = new ReporteDTO();
        dto.setIdTransaccion(solicitud.getId());
        dto.setFecha(solicitud.getFechaRegistro() == null ? "" : solicitud.getFechaRegistro().format(FORMATO_FECHA));
        dto.setCampanaTitulo(solicitud.getTipoAyuda());
        dto.setDistrito("-");
        dto.setTipoDonacion("Solicitud");
        dto.setValorMonto(solicitud.getDescripcion());
        dto.setEstado(solicitud.getEstado());
        return dto;
    }

    private int extraerEntero(String texto) {
        if (texto == null) {
            return 0;
        }
        String digitos = texto.replaceAll("\\D+", "");
        return digitos.isBlank() ? 0 : Integer.parseInt(digitos);
    }
}
