package pe.utp.proyectofinal.dto;

public class CampanaRequest {

    private String nombre;
    private String descripcion;
    private Integer metaDias;
    private Integer porcentaje;
    private String ubicacion;
    private Integer beneficiarios;
    private Integer meta;

    public CampanaRequest() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

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
