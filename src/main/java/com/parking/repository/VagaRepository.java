package com.parking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.parking.entity.Vaga;

public interface VagaRepository extends JpaRepository<Vaga, Long> {
    
    Optional<Vaga> findByCodigo(String codigo);
}
