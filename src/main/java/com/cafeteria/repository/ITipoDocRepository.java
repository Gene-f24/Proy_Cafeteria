package com.cafeteria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cafeteria.model.TipoDoc;

@Repository
public interface ITipoDocRepository extends JpaRepository<TipoDoc, Integer> {

}
