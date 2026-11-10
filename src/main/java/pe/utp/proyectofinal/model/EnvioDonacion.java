package pe.utp.proyectofinal.model;

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
@Table(name = "envio_donaciones")
public class EnvioDonacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_envio_donaciones")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "donacion_id", nullable = false, unique = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "campana"})
    private Donacion donacion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "campana_id", nullable = false)
    @JsonIgnoreProperties({"donaciones", "hibernateLazyInitializer", "handler"})
    private Campana campana;

    @Column(nullable = false, length = 20)
    private String estado;

    private LocalDateTime fechaEnvio;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(length = 100)
    private String enviadoPor;

    @PrePersist
    public void prePersist() {
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDateTime.now();
        }
        if (this.estado == null || this.estado.isBlank()) {
            this.estado = "PENDIENTE";
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Donacion getDonacion() { return donacion; }
    public void setDonacion(Donacion donacion) { this.donacion = donacion; }
    public Campana getCampana() { return campana; }
    public void setCampana(Campana campana) { this.campana = campana; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public String getEnviadoPor() { return enviadoPor; }
    public void setEnviadoPor(String enviadoPor) { this.enviadoPor = enviadoPor; }

    public Long getDonacionId() {
        return donacion != null ? donacion.getId() : null;
    }

    public Long getCampanaId() {
        return campana != null ? campana.getId() : null;
    }

    public String getCampanaNombre() {
        return campana != null ? campana.getNombre() : null;
    }

    public String getCodigoLote() {
        return donacion != null ? donacion.getCodigoLote() : null;
    }
}
