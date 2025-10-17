package com.seminario.pasantias.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostulacionUpdateRequest {
    private Integer id;
    private String estado;
    private String observaciones;
}
