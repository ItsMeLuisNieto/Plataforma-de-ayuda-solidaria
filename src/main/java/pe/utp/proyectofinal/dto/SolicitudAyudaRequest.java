package pe.utp.proyectofinal.dto;

import jakarta.validation.constraints.NotBlank;

public class SolicitudAyudaRequest {

    @NotBlank(message = "El nombre del donante es obligatorio")
    private String nombreDonantes;

    @NotBlank(message = "El tipo de ayuda es obligatorio")
    private String tipoAyuda;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    public String getNombreDonantes() { return nombreDonantes; }
    public void setNombreDonantes(String nombreDonantes) { this.nombreDonantes = nombreDonantes; }
    public String getTipoAyuda() { return tipoAyuda; }
    public void setTipoAyuda(String tipoAyuda) { this.tipoAyuda = tipoAyuda; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
