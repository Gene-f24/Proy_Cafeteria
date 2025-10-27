package com.cafeteria.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="tb_tipousua")
public class TipoUsua {
	@Id
	private int idtipousua;
	private String descripcion;

}
