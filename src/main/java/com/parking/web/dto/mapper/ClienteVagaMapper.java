package com.parking.web.dto.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import com.parking.entity.ClienteVaga;
import com.parking.web.dto.EstacionamentoCreateDTO;
import com.parking.web.dto.EstacionamentoResponseDTO;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClienteVagaMapper {

    public static ClienteVaga toClienteVaga(EstacionamentoCreateDTO dto) {
        return new ModelMapper().map(dto, ClienteVaga.class);
    }

    public static EstacionamentoResponseDTO toClienteVagaDto(ClienteVaga clienteVaga) {
        return new ModelMapper().map(clienteVaga, EstacionamentoResponseDTO.class);
    }
}
