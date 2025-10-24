package com.cafeteria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cafeteria.model.Venta;

@Repository
public interface IVentaRepository extends JpaRepository<Venta, Integer>{
	
}
