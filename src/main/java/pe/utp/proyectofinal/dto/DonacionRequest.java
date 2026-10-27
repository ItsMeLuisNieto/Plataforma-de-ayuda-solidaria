package pe.utp.proyectofinal.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DonacionRequest {

    private String codigoLote;

    @NotBlank(message = "El nombre del donante es obligatorio")
    private String nombreDonante;

    private String producto;

    @Min(value = 1, message = "La cantidad debe ser mayor que cero")
    private int cantidad;

    private String estado;

    private String tipoDonacion;

    private BigDecimal monto;

    private String medioPago;

    private String nroOperacion;

    private Long campanaId;

    private String telefono;

    private String correo;

    private LocalDateTime fechaTransferencia;

    @NotNull(message = "El monto es obligatorio para donaciones monetarias")
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

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
    public String getTipoDonacion() { return tipoDonacion; }
    public void setTipoDonacion(String tipoDonacion) { this.tipoDonacion = tipoDonacion; }
    public String getMedioPago() { return medioPago; }
    public void setMedioPago(String medioPago) { this.medioPago = medioPago; }
    public String getNroOperacion() { return nroOperacion; }
    public void setNroOperacion(String nroOperacion) { this.nroOperacion = nroOperacion; }
    public Long getCampanaId() { return campanaId; }
    public void setCampanaId(Long campanaId) { this.campanaId = campanaId; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public LocalDateTime getFechaTransferencia() { return fechaTransferencia; }
    public void setFechaTransferencia(LocalDateTime fechaTransferencia) { this.fechaTransferencia = fechaTransferencia; }
}
