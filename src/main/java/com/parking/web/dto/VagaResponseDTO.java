package com.parking.web.dto;

import lombok.*;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor
public class VagaResponseDTO {
    
    private Long id;
    private String codigo;
    private String status;
}
