package pe.utp.proyectofinal.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TestimonioRequest {

    @JsonAlias({"autor", "usuario", "name", "author"})
    private String nombre;

    @JsonAlias({"cargo", "ocupacion", "tipo", "role"})
    private String rol;

    @JsonAlias({"mensaje", "contenido", "opinion", "texto", "comment", "message"})
    private String comentario;

    public TestimonioRequest() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
}
