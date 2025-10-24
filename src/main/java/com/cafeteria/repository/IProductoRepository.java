package com.cafeteria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cafeteria.model.Producto;

@Repository
public interface IProductoRepository extends JpaRepository< Producto, Integer> {

}
