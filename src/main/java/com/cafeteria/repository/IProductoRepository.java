package com.cafeteria.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cafeteria.model.Producto;

@Repository
public interface IProductoRepository extends JpaRepository< Producto, Integer> {
	 // Busca por descripción de producto o por descripcion de categoria (ambos parciales, sin distinguir mayúsculas)
    List<Producto> findByDesProdContainingIgnoreCaseOrObjCat_DesCategContainingIgnoreCase(String desProd, String desCateg);	
}
