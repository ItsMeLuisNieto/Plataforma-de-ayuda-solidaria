package pe.utp.proyectofinal.dto;
import java.math.BigDecimal;

// --- Muestras para KPIs (Indicador Clave de Rendimiento) Generales ---

public class ReporteDTO {
    private BigDecimal totalMontoRecaudado;
    private Integer totalBeneficiariosAtendidos;
    private Integer totalCampanasActivas;
    private Integer totalBienesStock;
    private Long totalRecepciones;
    private Long totalEnvios;

    // --- Propiedades de la tabla de detalle de transaccion y apoyo ---
    private Long idTransaccion;
    private String codigoTransaccion;
    private String fecha;
    private String campanaTitulo;
    private String distrito;
    private String tipoDonacion; // Monetaria o Bienes/Especies
    private String valorMonto; // Ej: "S/ 150.00" o "45 prendas"
    private String estado;

    public ReporteDTO() {}

	public BigDecimal getTotalMontoRecaudado() {
		return totalMontoRecaudado;
	}
	public void setTotalMontoRecaudado(BigDecimal totalMontoRecaudado) {
		this.totalMontoRecaudado = totalMontoRecaudado;
	}
	public Integer getTotalBeneficiariosAtendidos() {
		return totalBeneficiariosAtendidos;
	}
	public void setTotalBeneficiariosAtendidos(Integer totalBeneficiariosAtendidos) {
		this.totalBeneficiariosAtendidos = totalBeneficiariosAtendidos;
	}
	public Integer getTotalCampanasActivas() {
		return totalCampanasActivas;
	}
	public void setTotalCampanasActivas(Integer totalCampanasActivas) {
		this.totalCampanasActivas = totalCampanasActivas;
	}
	public Integer getTotalBienesStock() {
		return totalBienesStock;
	}
	public void setTotalBienesStock(Integer totalBienesStock) {
		this.totalBienesStock = totalBienesStock;
	}
	public Long getTotalRecepciones() {
		return totalRecepciones;
	}
	public void setTotalRecepciones(Long totalRecepciones) {
		this.totalRecepciones = totalRecepciones;
	}
	public Long getTotalEnvios() {
		return totalEnvios;
	}
	public void setTotalEnvios(Long totalEnvios) {
		this.totalEnvios = totalEnvios;
	}
	public Long getIdTransaccion() {
		return idTransaccion;
	}
	public void setIdTransaccion(Long idTransaccion) {
		this.idTransaccion = idTransaccion;
	}
	public String getCodigoTransaccion() {
		return codigoTransaccion;
	}
	public void setCodigoTransaccion(String codigoTransaccion) {
		this.codigoTransaccion = codigoTransaccion;
	}
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public String getCampanaTitulo() {
		return campanaTitulo;
	}
	public void setCampanaTitulo(String campanaTitulo) {
		this.campanaTitulo = campanaTitulo;
	}
	public String getDistrito() {
		return distrito;
	}
	public void setDistrito(String distrito) {
		this.distrito = distrito;
	}
	public String getTipoDonacion() {
		return tipoDonacion;
	}
	public void setTipoDonacion(String tipoDonacion) {
		this.tipoDonacion = tipoDonacion;
	}
	public String getValorMonto() {
		return valorMonto;
	}
	public void setValorMonto(String valorMonto) {
		this.valorMonto = valorMonto;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
    
}
