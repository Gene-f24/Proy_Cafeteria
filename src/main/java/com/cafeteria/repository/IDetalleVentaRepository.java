package com.cafeteria.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cafeteria.model.DetalleVenta;

@Repository
public interface IDetalleVentaRepository extends JpaRepository< DetalleVenta, Integer> {
	//Buscar todos los detalles que pertenecen a una venta especifica
    List<DetalleVenta> findByObjVenta_IdVenta(int idVenta);
}
