package pe.utp.proyectofinal.service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.utp.proyectofinal.dto.ReporteDTO;
import pe.utp.proyectofinal.model.Campana;
import pe.utp.proyectofinal.model.Donacion;
import pe.utp.proyectofinal.model.EnvioDonacion;
import pe.utp.proyectofinal.model.RecepcionDonacion;
import pe.utp.proyectofinal.repository.CampanaRepository;
import pe.utp.proyectofinal.repository.DonacionRepository;
import pe.utp.proyectofinal.repository.EnvioDonacionRepository;
import pe.utp.proyectofinal.repository.RecepcionDonacionRepository;

@Service
public class ReporteService {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final CampanaRepository campanaRepository;
    private final DonacionRepository donacionRepository;
    private final RecepcionDonacionRepository recepcionDonacionRepository;
    private final EnvioDonacionRepository envioDonacionRepository;

    public ReporteService(CampanaRepository campanaRepository,
                          DonacionRepository donacionRepository,
                          RecepcionDonacionRepository recepcionDonacionRepository,
                          EnvioDonacionRepository envioDonacionRepository) {
        this.campanaRepository = campanaRepository;
        this.donacionRepository = donacionRepository;
        this.recepcionDonacionRepository = recepcionDonacionRepository;
        this.envioDonacionRepository = envioDonacionRepository;
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
        kpis.setTotalRecepciones(recepcionDonacionRepository.count());
        kpis.setTotalEnvios(envioDonacionRepository.count());
        return kpis;
    }

    @Transactional(readOnly = true)
    public List<ReporteDTO> obtenerMovimientos() {
        List<ReporteDTO> movimientos = new ArrayList<>();

        recepcionDonacionRepository.findAll().stream()
                .filter(r -> "CONFIRMADO".equalsIgnoreCase(r.getEstado()))
                .map(this::mapearRecepcion)
                .forEach(movimientos::add);

        envioDonacionRepository.findAll().stream()
                .filter(e -> "ENVIADO".equalsIgnoreCase(e.getEstado()))
                .map(this::mapearEnvio)
                .forEach(movimientos::add);

        movimientos.sort(Comparator.comparing(dto -> dto.getFecha() == null ? "" : dto.getFecha(), Comparator.reverseOrder()));
        return movimientos;
    }



    private ReporteDTO mapearRecepcion(RecepcionDonacion recepcion) {
        ReporteDTO dto = new ReporteDTO();
        Donacion donacion = recepcion.getDonacion();
        dto.setIdTransaccion(recepcion.getId());
        dto.setCodigoTransaccion("REC-" + recepcion.getId());
        dto.setFecha(recepcion.getFechaRevision() != null
                ? recepcion.getFechaRevision().format(FORMATO_FECHA)
                : (recepcion.getFechaSolicitud() == null ? "" : recepcion.getFechaSolicitud().format(FORMATO_FECHA)));
        dto.setCampanaTitulo(recepcion.getCampana() != null ? recepcion.getCampana().getNombre() : (donacion != null ? donacion.getProducto() : "-"));
        dto.setDistrito(recepcion.getCampana() != null ? recepcion.getCampana().getUbicacion() : "-");
        dto.setTipoDonacion("Recepción");
        dto.setValorMonto(donacion != null
                ? donacion.getCantidad() + (donacion.getCantidad() == 1 ? " unidad" : " unidades") + " (" + donacion.getProducto() + ")"
                : "-");
        dto.setEstado("RECIBIDO");
        return dto;
    }

    private ReporteDTO mapearEnvio(EnvioDonacion envio) {
        ReporteDTO dto = new ReporteDTO();
        Donacion donacion = envio.getDonacion();
        dto.setIdTransaccion(envio.getId());
        dto.setCodigoTransaccion("ENV-" + envio.getId());
        dto.setFecha(envio.getFechaEnvio() != null
                ? envio.getFechaEnvio().format(FORMATO_FECHA)
                : (envio.getFechaRegistro() == null ? "" : envio.getFechaRegistro().format(FORMATO_FECHA)));
        dto.setCampanaTitulo(envio.getCampana() != null ? envio.getCampana().getNombre() : (donacion != null ? donacion.getProducto() : "-"));
        dto.setDistrito(envio.getCampana() != null ? envio.getCampana().getUbicacion() : "-");
        dto.setTipoDonacion("Envío");
        dto.setValorMonto(donacion != null
                ? donacion.getCantidad() + (donacion.getCantidad() == 1 ? " unidad" : " unidades") + " (" + donacion.getProducto() + ")"
                : "-");
        dto.setEstado("ENVIADO");
        return dto;
    }
}

