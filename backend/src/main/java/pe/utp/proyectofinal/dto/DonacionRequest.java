package pe.utp.proyectofinal.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DonacionRequest {

    @JsonAlias({"campana_id", "idCampana", "id_campana", "campana"})
    private Long campanaId;

    @JsonAlias({"codigo_lote", "lote", "codigo"})
    private String codigoLote;

    @JsonAlias({"donante", "nombre", "nombre_donante", "donor"})
    private String nombreDonante;

    @JsonAlias({"item", "descripcion", "articulo", "bien", "product"})
    private String producto;

    @JsonAlias({"monto", "unidades", "total", "quantity"})
    private Integer cantidad;

    @JsonAlias({"status"})
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
