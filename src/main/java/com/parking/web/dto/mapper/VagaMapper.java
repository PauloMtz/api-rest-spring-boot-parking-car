package com.parking.web.dto.mapper;

import org.modelmapper.ModelMapper;

import com.parking.entity.Vaga;
import com.parking.web.dto.VagaCreateDTO;
import com.parking.web.dto.VagaResponseDTO;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VagaMapper {

    public static Vaga toVaga(VagaCreateDTO dto) {
        return new ModelMapper().map(dto, Vaga.class);
    }

    public static VagaResponseDTO toVagaDto(Vaga vaga) {
        return new ModelMapper().map(vaga, VagaResponseDTO.class);
    }
}
