package pe.utp.proyectofinal.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TestimonioRequest {

    @JsonProperty("nombre")
    @JsonAlias({"autor", "usuario", "name", "author", "persona", "nombreDonante"})
    private String nombre;

    @JsonProperty("rol")
    @JsonAlias({"cargo", "ocupacion", "tipo", "role", "puesto"})
    private String rol;

    @JsonProperty("comentario")
    @JsonAlias({"mensaje", "contenido", "opinion", "texto", "comment", "message", "descripcion", "testimonio"})
    private String comentario;

    public TestimonioRequest() {}

    public TestimonioRequest(String nombre, String rol, String comentario) {
        this.nombre = nombre;
        this.rol = rol;
        this.comentario = comentario;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
}
