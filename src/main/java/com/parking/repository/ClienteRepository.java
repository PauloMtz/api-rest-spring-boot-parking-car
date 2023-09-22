package com.parking.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.parking.entity.Cliente;
import com.parking.repository.projection.ClienteProjection;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    @Query("select c from Cliente c")
    Page<ClienteProjection> findAllPageable(Pageable pageable);

    Cliente findByUsuarioId(Long id);

    Optional<Cliente> findByCpf(String cpf);
}
