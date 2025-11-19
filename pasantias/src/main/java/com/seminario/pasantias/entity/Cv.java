package com.seminario.pasantias.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cv {
    private Integer idCv;
    private String nombreArchivo;
    private byte[] datosCv;
    private LocalDateTime fechaSubida;
    private Integer idEstudiante;
}
