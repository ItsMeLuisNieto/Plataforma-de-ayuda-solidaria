package pe.utp.proyectofinal.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import pe.utp.proyectofinal.enums.MedioPago;
import pe.utp.proyectofinal.enums.TipoDonacion;

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

    @Column(length = 100)
    private String producto;

    @Column(nullable = false)
    private int cantidad;

    @Column(nullable = false, length = 30)
    private String estado;

    @Column(nullable = false)
    private LocalDateTime fechaIngreso;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_donacion")
    private TipoDonacion tipoDonacion;

    @Column(precision = 10, scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(name = "medio_pago")
    private MedioPago medioPago;

    @Column(name = "nro_operacion", length = 100)
    private String nroOperacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campana_id")
    private Campana campana;

    @Column(name = "fecha_verificacion")
    private LocalDateTime fechaVerificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verificado_por_id")
    private Usuario verificadoPor;

    @Column(name = "motivo_rechazo", length = 500)
    private String motivoRechazo;

    @Column(length = 100)
    private String telefono;

    @Column(length = 150)
    private String correo;

    @Column(name = "fecha_transferencia")
    private LocalDateTime fechaTransferencia;

    @PrePersist
    public void prePersist() {
        this.fechaIngreso = LocalDateTime.now();
        if (this.codigoLote == null || this.codigoLote.isBlank()) {
            this.codigoLote = "LOTE-" + System.currentTimeMillis();
        }
        if (this.tipoDonacion == null) {
            this.tipoDonacion = TipoDonacion.BIENES;
        }
        if (this.cantidad == 0 && this.tipoDonacion == TipoDonacion.BIENES) {
            this.cantidad = 1;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public LocalDateTime getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDateTime fechaIngreso) { this.fechaIngreso = fechaIngreso; }
    public TipoDonacion getTipoDonacion() { return tipoDonacion; }
    public void setTipoDonacion(TipoDonacion tipoDonacion) { this.tipoDonacion = tipoDonacion; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public MedioPago getMedioPago() { return medioPago; }
    public void setMedioPago(MedioPago medioPago) { this.medioPago = medioPago; }
    public String getNroOperacion() { return nroOperacion; }
    public void setNroOperacion(String nroOperacion) { this.nroOperacion = nroOperacion; }
    public Campana getCampana() { return campana; }
    public void setCampana(Campana campana) { this.campana = campana; }
    public LocalDateTime getFechaVerificacion() { return fechaVerificacion; }
    public void setFechaVerificacion(LocalDateTime fechaVerificacion) { this.fechaVerificacion = fechaVerificacion; }
    public Usuario getVerificadoPor() { return verificadoPor; }
    public void setVerificadoPor(Usuario verificadoPor) { this.verificadoPor = verificadoPor; }
    public String getMotivoRechazo() { return motivoRechazo; }
    public void setMotivoRechazo(String motivoRechazo) { this.motivoRechazo = motivoRechazo; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public LocalDateTime getFechaTransferencia() { return fechaTransferencia; }
    public void setFechaTransferencia(LocalDateTime fechaTransferencia) { this.fechaTransferencia = fechaTransferencia; }
}
