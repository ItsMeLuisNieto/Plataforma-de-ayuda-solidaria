package pe.utp.proyectofinal.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "donaciones")
public class Donacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String codigoLote;

    @Column(nullable = false, length = 100)
    private String nombreDonante;

    @Column(nullable = false, length = 100)
    private String producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false, length = 30)
    private String estado;

    @Column(nullable = false)
    private LocalDateTime fechaIngreso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campana_id", nullable = false)
    @JsonIgnoreProperties({"donaciones", "hibernateLazyInitializer", "handler"})
    private Campana campana;

    @PrePersist
    public void prePersist() {
        if (this.fechaIngreso == null) this.fechaIngreso = LocalDateTime.now();
        if (this.estado == null || this.estado.isBlank()) this.estado = "PENDIENTE";
        if (this.codigoLote == null || this.codigoLote.isBlank()) this.codigoLote = "LOTE-" + System.currentTimeMillis();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public LocalDateTime getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDateTime fechaIngreso) { this.fechaIngreso = fechaIngreso; }
    public Campana getCampana() { return campana; }
    public void setCampana(Campana campana) { this.campana = campana; }

    public Long getCampanaId() {
        return campana != null ? campana.getId() : null;
    }

    public String getCampanaTitulo() {
        return campana != null ? (campana.getNombre() != null ? campana.getNombre() : campana.getTitulo()) : null;
    }

    public String getCampanaNombre() {
        return campana != null ? (campana.getNombre() != null ? campana.getNombre() : campana.getTitulo()) : null;
    }
}
