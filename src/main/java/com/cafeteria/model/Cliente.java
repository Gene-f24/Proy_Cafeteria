package com.cafeteria.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tb_cliente")
@Data
public class Cliente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_cli;
    private String nom_cli;
    private int id_tipodoc;
    private String NroDoc_cli;
}