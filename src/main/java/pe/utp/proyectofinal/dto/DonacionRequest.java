package pe.utp.proyectofinal.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DonacionRequest {

    @JsonProperty("campanaId")
    @JsonAlias({"campana_id", "idCampana", "id_campana"})
    private Long campanaId;

    @JsonProperty("codigoLote")
    @JsonAlias({"codigo_lote", "lote", "codigo"})
    private String codigoLote;

    @JsonProperty("nombreDonante")
    @JsonAlias({"donante", "nombre", "nombre_donante", "donor", "usuario", "autor"})
    private String nombreDonante;

    @JsonProperty("producto")
    @JsonAlias({"item", "descripcion", "articulo", "bien", "product", "tipo"})
    private String producto;

    @JsonProperty("cantidad")
    @JsonAlias({"monto", "unidades", "total", "quantity"})
    private Integer cantidad;

    @JsonProperty("estado")
    @JsonAlias({"status"})
    private String estado;

    public DonacionRequest() {}

    public DonacionRequest(Long campanaId, String codigoLote, String nombreDonante, String producto, Integer cantidad, String estado) {
        this.campanaId = campanaId;
        this.codigoLote = codigoLote;
        this.nombreDonante = nombreDonante;
        this.producto = producto;
        this.cantidad = cantidad;
        this.estado = estado;
    }

    public Long getCampanaId() { return campanaId; }
    public void setCampanaId(Long campanaId) { this.campanaId = campanaId; }

    @JsonProperty("campana")
    public void setCampana(Object campana) {
        if (campana instanceof Number num) {
            this.campanaId = num.longValue();
        } else if (campana instanceof Map<?, ?> map) {
            Object idVal = map.get("id");
            if (idVal instanceof Number numId) {
                this.campanaId = numId.longValue();
            } else if (idVal != null) {
                try {
                    this.campanaId = Long.parseLong(idVal.toString());
                } catch (NumberFormatException ignored) {}
            }
        } else if (campana instanceof String str && !str.isBlank()) {
            try {
                this.campanaId = Long.parseLong(str.trim());
            } catch (NumberFormatException ignored) {}
        }
    }

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
