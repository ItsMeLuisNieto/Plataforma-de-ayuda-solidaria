package pe.utp.proyectofinal.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class CampanaRequest {

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @Min(value = 1, message = "La campaña no puede durar menos de un día")
    @Max(value = 365, message = "La campaña no puede durar más de un año")
    private int metaDias;

    @Min(value = 0, message = "El porcentaje no puede ser negativo")
    @Max(value = 100, message = "El porcentaje no puede superar 100")
    private int porcentaje;

    @NotBlank(message = "La ubicación es obligatoria")
    private String ubicacion;

    @Min(value = 1, message = "Debe haber al menos un beneficiario")
    @Max(value = 1_000_000, message = "Los beneficiarios no deben ser mayor a 1M")
    private int beneficiarios;

    @Min(value = 0, message = "La meta no puede ser negativa")
    private int meta;

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public int getMetaDias() { return metaDias; }
    public void setMetaDias(int metaDias) { this.metaDias = metaDias; }
    public int getPorcentaje() { return porcentaje; }
    public void setPorcentaje(int porcentaje) { this.porcentaje = porcentaje; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public int getBeneficiarios() { return beneficiarios; }
    public void setBeneficiarios(int beneficiarios) { this.beneficiarios = beneficiarios; }
    public int getMeta() { return meta; }
    public void setMeta(int meta) { this.meta = meta; }
}
