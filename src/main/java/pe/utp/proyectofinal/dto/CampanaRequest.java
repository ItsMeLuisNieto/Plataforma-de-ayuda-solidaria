package pe.utp.proyectofinal.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class CampanaRequest {

    @NotBlank(message = "El nombre de la campaña es obligatorio")
    @JsonProperty("nombre")
    @JsonAlias({"titulo", "name"})
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @Min(value = 1, message = "La campaña no puede durar menos de un día")
    @Max(value = 365, message = "La campaña no puede durar más de un año")
    private Integer metaDias;

    @Min(value = 0, message = "El porcentaje no puede ser negativo")
    @Max(value = 100, message = "El porcentaje no puede superar 100")
    private Integer porcentaje;

    @NotBlank(message = "La ubicación es obligatoria")
    private String ubicacion;

    @Min(value = 1, message = "Debe haber al menos un beneficiario")
    @Max(value = 1_000_000, message = "Los beneficiarios no deben ser mayor a 1M")
    private Integer beneficiarios;

    @Min(value = 0, message = "La meta no puede ser negativa")
    private Integer meta;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTitulo() { return nombre; }
    public void setTitulo(String titulo) { if ((this.nombre == null || this.nombre.isBlank()) && titulo != null) { this.nombre = titulo; } }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Integer getMetaDias() { return metaDias; }
    public void setMetaDias(Integer metaDias) { this.metaDias = metaDias; }
    public Integer getPorcentaje() { return porcentaje; }
    public void setPorcentaje(Integer porcentaje) { this.porcentaje = porcentaje; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public Integer getBeneficiarios() { return beneficiarios; }
    public void setBeneficiarios(Integer beneficiarios) { this.beneficiarios = beneficiarios; }
    public Integer getMeta() { return meta; }
    public void setMeta(Integer meta) { this.meta = meta; }
}

