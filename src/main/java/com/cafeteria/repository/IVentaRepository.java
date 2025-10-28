package com.cafeteria.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.cafeteria.model.Venta;

@Repository
public interface IVentaRepository extends JpaRepository<Venta, Integer>{
	List<Venta> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin); 
}
