package com.cafeteria.model;



import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    @Column(name = "id_cli")
    private Integer idCli;
    //DNI,RUC,Carnet 
    @Column(name = "nro_doc_cli")
    private String nroDocCli;
    private LocalDateTime fecha;
    private Double subtotal;
    private Double igv;
    private Double total;
    

    
    //Relacion con Detalle de Venta
    @OneToMany(mappedBy = "objVenta", cascade = CascadeType.ALL,  orphanRemoval = true )
    private List<DetalleVenta> listadetalles;
    // mappedby-- no crea la columna, solo "mapea" la relacion.
    // cascade -- Cuando guardas una Venta, también se guardan sus DetalleVenta
    //orphanRemoval-- detalle que se elimina de la lista, también se borra de la DB
    
    //Asigna fecha automatica 
    @PrePersist
    public void asignarFecha() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }

}
