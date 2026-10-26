package pe.utp.proyectofinal.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class DonacionRequest {

    private String codigoLote;

    @NotBlank(message = "El nombre del donante es obligatorio")
    private String nombreDonante;

    @NotBlank(message = "El producto es obligatorio")
    private String producto;

    @Min(value = 1, message = "La cantidad debe ser mayor que cero")
    private int cantidad;

    private String estado;

    public String getCodigoLote() { return codigoLote; }
    public void setCodigoLote(String codigoLote) { this.codigoLote = codigoLote; }
    public String getNombreDonante() { return nombreDonante; }
    public void setNombreDonante(String nombreDonante) { this.nombreDonante = nombreDonante; }
    public String getProducto() { return producto; }
    public void setProducto(String producto) { this.producto = producto; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
