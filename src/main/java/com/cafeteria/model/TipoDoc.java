package com.cafeteria.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="tb_tipodoc")
@Data
public class TipoDoc {
	@Id
	private int id_tipodoc;
	private String nom_tipodoc;	
}
