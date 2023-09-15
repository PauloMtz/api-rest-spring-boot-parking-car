package com.parking.web.dto.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import com.parking.entity.Usuario;
import com.parking.web.dto.UsuarioCreateDTO;
import com.parking.web.dto.UsuarioResponseDTO;

public class UsuarioMapper {
    
    public static Usuario toUsuario(UsuarioCreateDTO createDTO) {
        return new ModelMapper().map(createDTO, Usuario.class);
    }

    public static UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        // eliminar a substring ROLE_
        String role = usuario.getRole().name().substring("ROLE_".length());
        PropertyMap<Usuario, UsuarioResponseDTO> props = 
            new PropertyMap<Usuario,UsuarioResponseDTO>() {

                @Override
                protected void configure() {
                    map().setRole(role);
                }
                
            };
        
        ModelMapper mapper = new ModelMapper();
        mapper.addMappings(props);

        return mapper.map(usuario, UsuarioResponseDTO.class);
    }

    public static List<UsuarioResponseDTO> toUsuariosListDTO(List<Usuario> usuarios) {

        return usuarios.stream()
            .map(user -> toResponseDTO(user)).collect(Collectors.toList());
    }
}
