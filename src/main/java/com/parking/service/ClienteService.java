package com.parking.service;

//import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.parking.entity.Cliente;
import com.parking.exception.CpfUniqueViolationException;
import com.parking.exception.EntityNotFoundException;
import com.parking.repository.ClienteRepository;
import com.parking.repository.projection.ClienteProjection;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ClienteService {
    
    private final ClienteRepository repository;

    @Transactional
    public Cliente salvar(Cliente cliente) {
        try {
            return repository.save(cliente);
        } catch (DataIntegrityViolationException ex) {
            throw new CpfUniqueViolationException(
                String.format("CPF '%s' já cadastrado no sistema", cliente.getCpf()));
        }
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(
            () -> new EntityNotFoundException(
                String.format("Cliente id=%s não encontrado", id)));
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorUsuarioId(Long id) {
        return repository.findByUsuarioId(id);
    }

    /*@Transactional(readOnly = true)
    public List<Cliente> buscarTodos() {
        return repository.findAll(Sort.by("nome"));
    }*/

    @Transactional(readOnly = true)
    public Page<ClienteProjection> buscarTodos(Pageable pageable) {
        return repository.findAllPageable(pageable);
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorCpf(String cpf) {
        return repository.findByCpf(cpf).orElseThrow(
            () -> new EntityNotFoundException(String.format("Cliente com CPF '%s' não encontrado", cpf))
        );
    }
}
