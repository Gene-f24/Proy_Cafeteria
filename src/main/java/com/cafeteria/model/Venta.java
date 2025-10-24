package com.cafeteria.model;


import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "tb_ventas")
@Data
public class Venta {   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_venta")
    private int idVenta;
    private String fecha;
    private Double subtotal;
    private Double igv;
    private Double total;
    //comentar
    @Column(name = "id_cli")
    private Integer idCli;
    
    //Relacion con Cliente
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cli",
    insertable = false, updatable = false)
    private Cliente obj_cli;   
    
    //Relacion con Detalle de Venta
    @OneToMany(mappedBy = "objVenta", cascade = CascadeType.ALL,  orphanRemoval = true )
    private List<DetalleVenta> listadetalles;
    // mappedby-- no crea la columna, solo "mapea" la relacion.
    // cascade -- Cuando guardas una Venta, también se guardan sus DetalleVenta
    //orphanRemoval-- detalle que se elimina de la lista, también se borra de la DB
    
    //Asigna fecha automatica si no se envía desde el formulario
    @PrePersist
    public void asignarFecha() {
        if (fecha == null) {
            fecha = LocalDate.now().toString();
        }
    }
}
