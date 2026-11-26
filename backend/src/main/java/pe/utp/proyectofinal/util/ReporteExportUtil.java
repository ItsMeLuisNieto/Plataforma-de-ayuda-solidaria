package pe.utp.proyectofinal.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import pe.utp.proyectofinal.dto.ReporteDTO;

public class ReporteExportUtil {

    private static final SimpleDateFormat FECHA_FORMATEO = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public static ByteArrayInputStream exportarExcel(ReporteDTO kpis, List<ReporteDTO> movimientos) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            // Estilos
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle tituloStyle = workbook.createCellStyle();
            Font tituloFont = workbook.createFont();
            tituloFont.setBold(true);
            tituloFont.setFontHeightInPoints((short) 16);
            tituloStyle.setFont(tituloFont);
            tituloStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle datoStyle = workbook.createCellStyle();
            datoStyle.setBorderBottom(BorderStyle.THIN);
            datoStyle.setBorderTop(BorderStyle.THIN);
            datoStyle.setBorderRight(BorderStyle.THIN);
            datoStyle.setBorderLeft(BorderStyle.THIN);

            // Hoja 1: KPIs
            Sheet sheetKpis = workbook.createSheet("Resumen KPIs");
            int rowNumKpi = 0;
            
            Row tituloKpiRow = sheetKpis.createRow(rowNumKpi++);
            Cell tituloKpiCell = tituloKpiRow.createCell(0);
            tituloKpiCell.setCellValue("REPORTE DE IMPACTO - KALLPA SOLIDARY");
            tituloKpiCell.setCellStyle(tituloStyle);
            sheetKpis.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 1));
            
            rowNumKpi++; // Espacio
            
            // Cabeceras KPIs
            Row headerKpiRow = sheetKpis.createRow(rowNumKpi++);
            String[] kpiHeaders = {"INDICADOR", "VALOR"};
            for (int i = 0; i < kpiHeaders.length; i++) {
                Cell cell = headerKpiRow.createCell(i);
                cell.setCellValue(kpiHeaders[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Datos KPIs
            Object[][] kpiDatos = {
                {"Total Monto Recaudado", kpis.getTotalMontoRecaudado() != null ? "S/ " + kpis.getTotalMontoRecaudado() : "S/ 0.00"},
                {"Total Campañas Activas", kpis.getTotalCampanasActivas() != null ? kpis.getTotalCampanasActivas().toString() : "0"},
                {"Total Beneficiarios Atendidos", kpis.getTotalBeneficiariosAtendidos() != null ? kpis.getTotalBeneficiariosAtendidos().toString() + " Familias" : "0 Familias"},
                {"Total Bienes en Stock", kpis.getTotalBienesStock() != null ? kpis.getTotalBienesStock().toString() + " Artículos" : "0 Artículos"},
                {"Fecha de Generación", FECHA_FORMATEO.format(new Date())}
            };
            
            for (Object[] dato : kpiDatos) {
                Row row = sheetKpis.createRow(rowNumKpi++);
                Cell cell1 = row.createCell(0);
                cell1.setCellValue(dato[0].toString());
                cell1.setCellStyle(datoStyle);
                Cell cell2 = row.createCell(1);
                cell2.setCellValue(dato[1].toString());
                cell2.setCellStyle(datoStyle);
            }
            
            sheetKpis.autoSizeColumn(0);
            sheetKpis.autoSizeColumn(1);
            
            // Hoja 2: Movimientos
            Sheet sheetMovimientos = workbook.createSheet("Historial de Transacciones");
            int rowNumMov = 0;
            
            Row tituloMovRow = sheetMovimientos.createRow(rowNumMov++);
            Cell tituloMovCell = tituloMovRow.createCell(0);
            tituloMovCell.setCellValue("HISTORIAL DE TRANSACCIONES Y APOYOS");
            tituloMovCell.setCellStyle(tituloStyle);
            sheetMovimientos.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 6));
            
            rowNumMov++;
            
            // Cabeceras Movimientos
            Row headerMovRow = sheetMovimientos.createRow(rowNumMov++);
            String[] movHeaders = {"ID TRANSACCIÓN", "FECHA", "CAMPAÑA", "UBICACIÓN", "TIPO DONACIÓN", "MONTO/CANTIDAD", "ESTADO"};
            for (int i = 0; i < movHeaders.length; i++) {
                Cell cell = headerMovRow.createCell(i);
                cell.setCellValue(movHeaders[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Datos Movimientos
            if (movimientos != null && !movimientos.isEmpty()) {
                for (ReporteDTO mov : movimientos) {
                    Row row = sheetMovimientos.createRow(rowNumMov++);
                    row.createCell(0).setCellValue(mov.getIdTransaccion() != null ? "#TRX-" + mov.getIdTransaccion() : "");
                    row.createCell(1).setCellValue(mov.getFecha() != null ? mov.getFecha() : "");
                    row.createCell(2).setCellValue(mov.getCampanaTitulo() != null ? mov.getCampanaTitulo() : "");
                    row.createCell(3).setCellValue(mov.getDistrito() != null ? mov.getDistrito() : "");
                    row.createCell(4).setCellValue(mov.getTipoDonacion() != null ? mov.getTipoDonacion() : "");
                    row.createCell(5).setCellValue(mov.getValorMonto() != null ? mov.getValorMonto() : "");
                    row.createCell(6).setCellValue(mov.getEstado() != null ? mov.getEstado() : "");
                    
                    for (int i = 0; i < 7; i++) {
                        row.getCell(i).setCellStyle(datoStyle);
                    }
                }
            }
            
            for (int i = 0; i < 7; i++) {
                sheetMovimientos.autoSizeColumn(i);
            }
            
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public static ByteArrayInputStream exportarPDF(ReporteDTO kpis, List<ReporteDTO> movimientos) throws DocumentException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, out);
        document.open();
        
        // Título
        com.lowagie.text.Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph titulo = new Paragraph("REPORTE DE IMPACTO - KALLPA SOLIDARY", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        document.add(titulo);
        
        // Fecha
        com.lowagie.text.Font fechaFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Paragraph fecha = new Paragraph("Fecha de generación: " + FECHA_FORMATEO.format(new Date()), fechaFont);
        fecha.setAlignment(Element.ALIGN_RIGHT);
        fecha.setSpacingAfter(15);
        document.add(fecha);
        
        // Sección KPIs
        com.lowagie.text.Font subtituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Paragraph subtituloKpis = new Paragraph("Resumen de Indicadores Clave (KPIs)", subtituloFont);
        subtituloKpis.setSpacingAfter(10);
        document.add(subtituloKpis);
        
        PdfPTable tablaKpis = new PdfPTable(2);
        tablaKpis.setWidthPercentage(100);
        tablaKpis.setSpacingAfter(20);
        
        // Cabeceras
        PdfPCell headerCell1 = new PdfPCell(new Phrase("INDICADOR", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        headerCell1.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
        headerCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell1.setPadding(5);
        tablaKpis.addCell(headerCell1);
        
        PdfPCell headerCell2 = new PdfPCell(new Phrase("VALOR", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        headerCell2.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
        headerCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell2.setPadding(5);
        tablaKpis.addCell(headerCell2);
        
        // Datos
        tablaKpis.addCell(crearCelda("Total Monto Recaudado"));
        tablaKpis.addCell(crearCelda(kpis.getTotalMontoRecaudado() != null ? "S/ " + kpis.getTotalMontoRecaudado() : "S/ 0.00"));
        
        tablaKpis.addCell(crearCelda("Total Campañas Activas"));
        tablaKpis.addCell(crearCelda(kpis.getTotalCampanasActivas() != null ? kpis.getTotalCampanasActivas().toString() : "0"));
        
        tablaKpis.addCell(crearCelda("Total Beneficiarios Atendidos"));
        tablaKpis.addCell(crearCelda(kpis.getTotalBeneficiariosAtendidos() != null ? kpis.getTotalBeneficiariosAtendidos().toString() + " Familias" : "0 Familias"));
        
        tablaKpis.addCell(crearCelda("Total Bienes en Stock"));
        tablaKpis.addCell(crearCelda(kpis.getTotalBienesStock() != null ? kpis.getTotalBienesStock().toString() + " Artículos" : "0 Artículos"));
        
        document.add(tablaKpis);
        
        // Sección Movimientos
        Paragraph subtituloMov = new Paragraph("Historial de Transacciones y Apoyos", subtituloFont);
        subtituloMov.setSpacingAfter(10);
        document.add(subtituloMov);
        
        if (movimientos != null && !movimientos.isEmpty()) {
            PdfPTable tablaMov = new PdfPTable(7);
            tablaMov.setWidthPercentage(100);
            tablaMov.setSpacingAfter(20);
            float[] anchos = {0.8f, 1f, 1.5f, 0.8f, 0.9f, 1.1f, 0.8f};
            tablaMov.setWidths(anchos);
            
            // Cabeceras
            String[] movCabeceras = {"ID", "Fecha", "Campaña", "Ubic.", "Tipo", "Monto/Cant.", "Estado"};
            for (String cab : movCabeceras) {
                PdfPCell cellCab = new PdfPCell(new Phrase(cab, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8)));
                cellCab.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
                cellCab.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellCab.setPadding(4);
                tablaMov.addCell(cellCab);
            }
            
            // Datos
            for (ReporteDTO mov : movimientos) {
                tablaMov.addCell(crearCeldaPequena(mov.getIdTransaccion() != null ? "#TRX-" + mov.getIdTransaccion() : ""));
                tablaMov.addCell(crearCeldaPequena(mov.getFecha() != null ? mov.getFecha() : ""));
                tablaMov.addCell(crearCeldaPequena(mov.getCampanaTitulo() != null ? mov.getCampanaTitulo() : ""));
                tablaMov.addCell(crearCeldaPequena(mov.getDistrito() != null ? mov.getDistrito() : ""));
                tablaMov.addCell(crearCeldaPequena(mov.getTipoDonacion() != null ? mov.getTipoDonacion() : ""));
                tablaMov.addCell(crearCeldaPequena(mov.getValorMonto() != null ? mov.getValorMonto() : ""));
                tablaMov.addCell(crearCeldaPequena(mov.getEstado() != null ? mov.getEstado() : ""));
            }
            
            document.add(tablaMov);
        } else {
            Paragraph sinDatos = new Paragraph("No hay movimientos registrados.", fechaFont);
            sinDatos.setSpacingAfter(10);
            document.add(sinDatos);
        }
        
        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }
    
    private static PdfPCell crearCelda(String texto) {
        PdfPCell cell = new PdfPCell(new Phrase(texto != null ? texto : "", FontFactory.getFont(FontFactory.HELVETICA, 9)));
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        return cell;
    }
    
    private static PdfPCell crearCeldaPequena(String texto) {
        PdfPCell cell = new PdfPCell(new Phrase(texto != null ? texto : "", FontFactory.getFont(FontFactory.HELVETICA, 7)));
        cell.setPadding(3);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }
}
