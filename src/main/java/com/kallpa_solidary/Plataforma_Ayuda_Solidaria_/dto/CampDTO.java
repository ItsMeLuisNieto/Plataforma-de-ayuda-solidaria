package com.kallpa_solidary.Plataforma_Ayuda_Solidaria_.dto;

public class CampDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private String metaDias;
    private int porcentaje;
    private String ubicacion;
    private String beneficiarios;
    private String meta;


    public CampDTO() {
    }

    public CampDTO(Long id, String titulo, String descripcion, String metaDias, int porcentaje, String ubicacion, String beneficiarios, String meta) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.metaDias = metaDias;
        this.porcentaje = porcentaje;
        this.ubicacion = ubicacion;
        this.beneficiarios = beneficiarios;
        this.meta = meta;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getMetaDias() {
        return metaDias;
    }

    public void setMetaDias(String metaDias) {
        this.metaDias = metaDias;
    }

    public int getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(int porcentaje) {
        this.porcentaje = porcentaje;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getBeneficiarios() {
        return beneficiarios;
    }

    public void setBeneficiarios(String beneficiarios) {
        this.beneficiarios = beneficiarios;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }
}
