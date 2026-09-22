package com.kallpa_solidary.Plataforma_Ayuda_Solidaria_.dto;

public class TestimoniosDto {
    private Long id; //Se generará automaticamente por el backend
    private String nombre, rol, comentario;

    public TestimoniosDto(){}

    public TestimoniosDto(Long id, String nombre, String rol, String comentario) {
        this.id = id;
        this.nombre = nombre;
        this.rol = rol;
        this.comentario = comentario;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

}
