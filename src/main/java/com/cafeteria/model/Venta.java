package com.cafeteria.model;

import java.sql.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "tb_ventas")
@Data
public class Venta {   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_venta;
    private String fecha;
    private Double subtotal;
    private Double igv;
    private Double total;
    @ManyToOne
    @JoinColumn(name = "id_cli")
    private Cliente cliente;
    // mappedby-- no crea la columna, solo "mapea" la relación.
    // cascade -- Cuando guardas una Venta, también se guardan sus DetalleVenta
    //orphanRemoval-- detalle que se elimina de la lista, también se borra de la DB
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL,  orphanRemoval = true )
    private List<DetalleVenta> detalles;
}
