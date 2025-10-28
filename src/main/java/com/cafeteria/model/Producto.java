package com.cafeteria.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="tb_producto")
@Data
public class Producto {
    @Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id_prod;
    @Column(name="des_prod")
    private String desProd;
    private int stk_prod;
    private Double pre_prod;
    private int id_categ;
    //est
    //Join
    @ManyToOne
    @JoinColumn(name="id_categ",
    	insertable = false, updatable = false)
    private Categoria objCat;
}