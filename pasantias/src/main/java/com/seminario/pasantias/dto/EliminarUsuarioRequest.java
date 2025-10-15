package com.seminario.pasantias.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EliminarUsuarioRequest {
    private String nombre;
}