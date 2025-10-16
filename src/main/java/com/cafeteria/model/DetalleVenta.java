package com.cafeteria.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
@Entity
@Table(name="tb_detalle_venta")
@Data
public class DetalleVenta {
	 	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private int id_detalleventa;
	    @ManyToOne
	    @JoinColumn(name = "id_venta")
	    private Venta venta;
	    @ManyToOne
	    @JoinColumn(name = "id_prod")
	    private Producto producto;
	    private int cantidad;
	    private Double precio_unitario;
	    private Double subtotal;
}
