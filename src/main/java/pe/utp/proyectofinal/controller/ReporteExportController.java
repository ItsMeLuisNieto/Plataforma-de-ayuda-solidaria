package pe.utp.proyectofinal.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lowagie.text.DocumentException;

import pe.utp.proyectofinal.dto.ReporteDTO;
import pe.utp.proyectofinal.service.ReporteService;
import pe.utp.proyectofinal.util.ReporteExportUtil;

@RestController
@RequestMapping("/reportes")
public class ReporteExportController {

    private final ReporteService reporteService;

    public ReporteExportController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/exportar/excel")
    public ResponseEntity<InputStreamResource> exportarExcel(Authentication authentication) throws IOException {
        if (!esAdmin(authentication)) {
            return ResponseEntity.status(403).build();
        }
        
        ReporteDTO kpis = reporteService.obtenerKpis();
        List<ReporteDTO> movimientos = reporteService.obtenerMovimientos();
        
        ByteArrayInputStream bis = ReporteExportUtil.exportarExcel(kpis, movimientos);
        
        String filename = "Reporte_Kallpa_Solidary_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xlsx";
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + filename);
        
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(bis));
    }

    @GetMapping("/exportar/pdf")
    public ResponseEntity<InputStreamResource> exportarPDF(Authentication authentication) throws DocumentException, IOException {
        if (!esAdmin(authentication)) {
            return ResponseEntity.status(403).build();
        }
        
        ReporteDTO kpis = reporteService.obtenerKpis();
        List<ReporteDTO> movimientos = reporteService.obtenerMovimientos();
        
        ByteArrayInputStream bis = ReporteExportUtil.exportarPDF(kpis, movimientos);
        
        String filename = "Reporte_Kallpa_Solidary_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf";
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + filename);
        
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    private boolean esAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        if ("anonymousUser".equals(authentication.getName())) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }
}
