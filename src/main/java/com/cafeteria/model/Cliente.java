package com.cafeteria.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tb_cliente")
@Data
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cli")
    private int idCli;
    private String nom_cli;
    private int id_tipodoc;
    @Column(name="nro_doc_cli")
    private String nroDocCli ;
    //est
    @ManyToOne
    @JoinColumn(name = "id_tipodoc",
      insertable = false, updatable = false)
    private TipoDoc objTipoDoc;   
}