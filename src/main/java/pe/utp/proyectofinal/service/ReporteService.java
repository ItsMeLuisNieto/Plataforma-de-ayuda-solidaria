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
import pe.utp.proyectofinal.repository.CampanaRepository;
import pe.utp.proyectofinal.repository.DonacionRepository;

@Service
public class ReporteService {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final CampanaRepository campanaRepository;
    private final DonacionRepository donacionRepository;

    public ReporteService(CampanaRepository campanaRepository,
                          DonacionRepository donacionRepository) {
        this.campanaRepository = campanaRepository;
        this.donacionRepository = donacionRepository;
    }

    @Transactional(readOnly = true)
    public ReporteDTO obtenerKpis() {
        List<Campana> campanas = campanaRepository.findAll();
        ReporteDTO kpis = new ReporteDTO();
        kpis.setTotalMontoRecaudado(BigDecimal.ZERO);
        kpis.setTotalCampanasActivas(campanas.size());
        kpis.setTotalBeneficiariosAtendidos(campanas.stream()
                .mapToInt(Campana::getBeneficiarios)
                .sum());
        kpis.setTotalBienesStock(donacionRepository.findByEstado("CONFIRMADO").stream()
                .mapToInt(Donacion::getCantidad)
                .sum());
        return kpis;
    }

    @Transactional(readOnly = true)
    public List<ReporteDTO> obtenerMovimientos() {
        List<ReporteDTO> movimientos = new ArrayList<>();

        donacionRepository.findByEstado("CONFIRMADO").stream()
                .map(this::mapearDonacion)
                .forEach(movimientos::add);

        movimientos.sort(Comparator.comparing(ReporteDTO::getFecha).reversed());
        return movimientos;
    }

    private ReporteDTO mapearDonacion(Donacion donacion) {
        ReporteDTO dto = new ReporteDTO();
        dto.setIdTransaccion(donacion.getId());
        dto.setFecha(donacion.getFechaIngreso() == null ? "" : donacion.getFechaIngreso().format(FORMATO_FECHA));
        dto.setCampanaTitulo(donacion.getCampana() != null ? (donacion.getCampana().getNombre() != null ? donacion.getCampana().getNombre() : donacion.getCampana().getTitulo()) : donacion.getProducto());
        dto.setDistrito(donacion.getCampana() != null ? donacion.getCampana().getUbicacion() : "-");
        dto.setTipoDonacion("Bienes");
        dto.setValorMonto(donacion.getCantidad() + (donacion.getCantidad() == 1 ? " unidad" : " unidades") + " (" + donacion.getProducto() + ")");
        dto.setEstado(donacion.getEstado());
        return dto;
    }
}

