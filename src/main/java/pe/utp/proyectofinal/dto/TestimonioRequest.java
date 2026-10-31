package pe.utp.proyectofinal.dto;

public class TestimonioRequest {

    private String comentario;
    private Long usuarioId;

    public TestimonioRequest() {}

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
}
