package com.cafeteria.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "tb_categoria")
public class Categoria {
	@Id
	private int id_categ;
	private String des_categ;
}
