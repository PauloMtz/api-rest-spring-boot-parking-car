package com.parking.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.parking.entity.Vaga;
import com.parking.exception.CodigoUniqueViolationException;
import com.parking.exception.EntityNotFoundException;
import com.parking.repository.VagaRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class VagaService {

    private final VagaRepository repository;

    @Transactional
    public Vaga salvar(Vaga vaga) {
        try {
            return repository.save(vaga);
        } catch (DataIntegrityViolationException ex) {
            throw new CodigoUniqueViolationException(
                String.format("Vaga com código '%s' já cadastrada", vaga.getCodigo()));
        }
    }

    @Transactional(readOnly = true)
    public Vaga buscarPorCodigo(String codigo) {
        return repository.findByCodigo(codigo).orElseThrow(
            () -> new EntityNotFoundException(
                String.format("Vaga com código '%s' não foi encontrada", codigo)));
    }
}
