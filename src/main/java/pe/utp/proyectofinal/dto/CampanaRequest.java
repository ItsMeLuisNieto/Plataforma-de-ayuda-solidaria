package pe.utp.proyectofinal.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class CampanaRequest {

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotBlank(message = "La duración es obligatoria")
    private String metaDias;

    @Min(value = 0, message = "El porcentaje no puede ser negativo")
    @Max(value = 100, message = "El porcentaje no puede superar 100")
    private int porcentaje;

    @NotBlank(message = "La ubicación es obligatoria")
    private String ubicacion;

    @NotBlank(message = "Los beneficiarios son obligatorios")
    private String beneficiarios;

    @NotBlank(message = "La meta es obligatoria")
    private String meta;

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getMetaDias() { return metaDias; }
    public void setMetaDias(String metaDias) { this.metaDias = metaDias; }
    public int getPorcentaje() { return porcentaje; }
    public void setPorcentaje(int porcentaje) { this.porcentaje = porcentaje; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public String getBeneficiarios() { return beneficiarios; }
    public void setBeneficiarios(String beneficiarios) { this.beneficiarios = beneficiarios; }
    public String getMeta() { return meta; }
    public void setMeta(String meta) { this.meta = meta; }
}
