package com.parking.web.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor 
@AllArgsConstructor
public class ClienteResponseDTO {
    
    private Long id;
    private String nome;
    private String cpf;
}
