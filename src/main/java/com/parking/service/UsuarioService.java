package com.parking.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.parking.entity.Usuario;
import com.parking.exception.EntityNotFoundException;
import com.parking.exception.PasswordInvalidException;
import com.parking.exception.UsernameUniqueViolationException;
import com.parking.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UsuarioService {
    
    private final UsuarioRepository repository;

    private final PasswordEncoder encoder;

    @Transactional
    public Usuario salvar(Usuario usuario) {
        try {
            usuario.setPassword(encoder.encode(usuario.getPassword()));
            return repository.save(usuario);
        } catch (DataIntegrityViolationException ex) {
            throw new UsernameUniqueViolationException(
                String.format("O e-mail %s já está cadastrado", usuario.getUsername()));
        }
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(
            () -> new EntityNotFoundException(String.format("Usuário id=%s não encontrado", id))
        );
    }

    /*@Transactional
    public Usuario editarSenha(Long id, String password) {
        Usuario user = buscarPorId(id);
        user.setPassword(password);
        return user;
    }*/

    @Transactional
    public Usuario editarSenha(Long id, String senhaAtual, 
        String novaSenha, String confirmaSenha) {

        // confere se as senhas são iguais
        if (!novaSenha.equals(confirmaSenha)) {
            throw new PasswordInvalidException("As senhas não conferem");
        }

        // se passou pelo if, busca usuário
        Usuario user = buscarPorId(id);

        // verifica a senha atual informada
        //if (!user.getPassword().equals(senhaAtual)) {
        if (!encoder.matches(senhaAtual, user.getPassword())) {
            throw new PasswordInvalidException("Senha atual não confere");
        }

        // se passou, salva a nova senha
        user.setPassword(encoder.encode(novaSenha));
        return user;
    }

    @Transactional(readOnly = true)
    public List<Usuario> buscarTodos() {
        return repository.findAll(Sort.by("username"));
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorUsername(String username) {
        return repository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException(String.format("Usuario com %s não encontrado", username))
        );
    }

    @Transactional(readOnly = true)
    public Usuario.Role buscarRolePorUsername(String username) {
        return repository.findRoleByUsername(username);
    }
}
