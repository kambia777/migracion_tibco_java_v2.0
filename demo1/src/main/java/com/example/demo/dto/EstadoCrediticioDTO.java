package com.example.demo.dto;
//Clase DTO en tu aplicación cliente
public class EstadoCrediticioDTO {
	
    private Long id;
    private Long codigoCliente;
    private String nombre;
    private boolean esDeudor;
    private Double monto_pendiente;
    
    
	public EstadoCrediticioDTO(Long id, Long codigoCliente, String nombre, boolean esDeudor, Double monto_pendiente) {
		super();
		this.id = id;
		this.codigoCliente = codigoCliente;
		this.nombre = nombre;
		this.esDeudor = esDeudor;
		this.monto_pendiente = monto_pendiente;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getCodigo_cliente() {
		return codigoCliente;
	}
	public void setCodigo_cliente(Long codigoCliente) {
		this.codigoCliente = codigoCliente;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public boolean isEs_deudor() {
		return esDeudor;
	}
	public void setEs_deudor(boolean es_deudor) {
		this.esDeudor = es_deudor;
	}
	public Double getMonto_pendiente() {
		return monto_pendiente;
	}
	public void setMonto_pendiente(Double monto_pendiente) {
		this.monto_pendiente = monto_pendiente;
	}

    
}
