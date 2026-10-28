package pe.utp.proyectofinal.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CampanaRequest {

    @JsonProperty("nombre")
    @JsonAlias({"titulo", "name", "title"})
    private String nombre;

    @JsonAlias({"description", "detalle", "info"})
    private String descripcion;

    @JsonAlias({"dias", "meta_dias", "metaDias", "duracion"})
    private Integer metaDias;

    @JsonAlias({"porcentaje", "avance"})
    private Integer porcentaje;

    @JsonAlias({"ubicacion", "lugar", "ciudad", "distrito", "direccion"})
    private String ubicacion;

    @JsonAlias({"beneficiarios", "personas", "cantidad_beneficiarios", "cantidadBeneficiarios"})
    private Integer beneficiarios;

    @JsonAlias({"meta", "objetivo", "monto", "monto_meta", "montoMeta"})
    private Integer meta;

    public CampanaRequest() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTitulo() { return nombre; }
    public void setTitulo(String titulo) {
        if ((this.nombre == null || this.nombre.isBlank()) && titulo != null) {
            this.nombre = titulo;
        }
    }

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
