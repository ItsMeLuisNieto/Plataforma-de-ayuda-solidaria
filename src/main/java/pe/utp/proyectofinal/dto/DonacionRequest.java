package pe.utp.proyectofinal.dto;

public class DonacionRequest {

    private Long campanaId;
    private String codigoLote;
    private String nombreDonante;
    private String producto;
    private Integer cantidad;
    private String estado;

    public DonacionRequest() {}

    public Long getCampanaId() { return campanaId; }
    public void setCampanaId(Long campanaId) { this.campanaId = campanaId; }

    public String getCodigoLote() { return codigoLote; }
    public void setCodigoLote(String codigoLote) { this.codigoLote = codigoLote; }

    public String getNombreDonante() { return nombreDonante; }
    public void setNombreDonante(String nombreDonante) { this.nombreDonante = nombreDonante; }

    public String getProducto() { return producto; }
    public void setProducto(String producto) { this.producto = producto; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
