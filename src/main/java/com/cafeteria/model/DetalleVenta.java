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
	 	@Column(name = "id_detalleventa")
	    private int idDetalle;
	 	//Para guardar y editar desde formularios
	 	@Column(name="id_venta")
	 	private Integer idVenta;
	 	private Integer id_prod;
	    private int cantidad;
	    private Double precio_unitario;
	    private Double subtotal;
	 	
	 	//est
	    @ManyToOne
	    @JoinColumn(name = "id_venta", insertable = false, updatable = false)
	    private Venta objVenta;

	    @ManyToOne
	    @JoinColumn(name = "id_prod", insertable = false, updatable = false)
	    private Producto objProd;
	    //est
	
	  
}
