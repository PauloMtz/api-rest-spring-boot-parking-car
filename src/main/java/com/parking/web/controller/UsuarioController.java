package com.parking.web.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.parking.entity.Usuario;
import com.parking.service.UsuarioService;
import com.parking.web.dto.UsuarioCreateDTO;
import com.parking.web.dto.UsuarioResponseDTO;
import com.parking.web.dto.UsuarioSenhaDTO;
import com.parking.web.dto.mapper.UsuarioMapper;
import com.parking.web.exception.ErrorMessage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Usuários", description = "CRUD de usuários")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v2/usuarios")
public class UsuarioController {
    
    private final UsuarioService service;

    @Operation(summary = "Cria usuário", description = "Sem restrição de acesso.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Recurso criado com êxito",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado no sistema",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Dados inválidos",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorMessage.class)))
        })
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> create(@Valid @RequestBody UsuarioCreateDTO createDTO) {
        Usuario user = service.salvar(UsuarioMapper.toUsuario(createDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioMapper.toResponseDTO(user));
    }

    @Operation(summary = "Busca usuário pelo ID", description = "Exige Token para acesso. Restrito a ADMIN|CLIENTE.",
        security = @SecurityRequirement(name = "security"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Recurso encontrado com êxito",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Recurso negado",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorMessage.class)))
        })
    @GetMapping("/{id}") // esse authentication abaixo utiliza as configurações da classe JwtUserDetails
    @PreAuthorize("hasRole('ADMIN') OR (hasRole('CLIENTE') AND #id == authentication.principal.id)")
    public ResponseEntity<UsuarioResponseDTO> getById(@PathVariable Long id) {
        Usuario user = service.buscarPorId(id);
        return ResponseEntity.ok(UsuarioMapper.toResponseDTO(user));
    }

    @Operation(summary = "Atualiza senha do usuário", 
        description = "Exige Token para acesso. Restrito a ADMIN|CLIENTE.",
        security = @SecurityRequirement(name = "security"),
        responses = {
            @ApiResponse(responseCode = "204", description = "Recurso atualizado com êxito"),
            @ApiResponse(responseCode = "400", description = "Senha não confere",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "Recurso negado",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Dados inválidos ou mal formatados",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorMessage.class)))
        })
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE') AND (#id == authentication.principal.id)")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, 
        @Valid @RequestBody UsuarioSenhaDTO usuarioDto) {

        service.editarSenha(id, usuarioDto.getSenhaAtual(), 
            usuarioDto.getNovaSenha(), usuarioDto.getConfirmaSenha());
        
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Lista todos os usuários", description = "Exige Token para acesso. Restrito a ADMIN.",
        security = @SecurityRequirement(name = "security"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Recurso encontrado com êxito",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Recurso negado",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorMessage.class)))
        })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioResponseDTO>> getAll() {
        List<Usuario> users = service.buscarTodos();
        return ResponseEntity.ok(UsuarioMapper.toUsuariosListDTO(users));
    }
}
