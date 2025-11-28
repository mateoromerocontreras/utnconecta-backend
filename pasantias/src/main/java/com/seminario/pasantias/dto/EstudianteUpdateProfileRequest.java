package com.seminario.pasantias.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstudianteUpdateProfileRequest {
    private String email;
    private String password;
}