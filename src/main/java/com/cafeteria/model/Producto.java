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
    private String des_prod;
    private String stk_prod;
    private Double pre_prod;
    private int id_categ;
}