package com.parking.web.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.br.CPF;

@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor
public class ClienteCreateDTO {
    
    @NotBlank
    @Size(min = 5, max = 100)
    private String nome;

    @Size(min = 11, max = 11)
    @CPF
    private String cpf;
}
