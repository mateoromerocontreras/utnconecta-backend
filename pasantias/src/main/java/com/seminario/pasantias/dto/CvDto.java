package com.seminario.pasantias.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CvDto {
    private Integer idCv;
    private String nombreArchivo;
    private LocalDateTime fechaSubida;
}
