package com.seminario.pasantias.dto.response;

import com.seminario.pasantias.entity.EstadoPostulacion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostulacionResponseDTO {
    private Integer idPostulacion;
    private LocalDate fechaPostulacion;
    private LocalDate fechaInicioContrato;
    private Integer duracionMeses;
    private EstadoPostulacion estado;
    private String observaciones;
    private LocalDate fechaCreacion;

    // Campos desnormalizados
    private Integer idPasantia;
    private String tituloPasantia;
    private String modalidad;
    private String nombreEmpresa;

    private Integer idEstudiante;
    private String nombreEstudiante;
    private String apellidoEstudiante;
    private String emailEstudiante;
    private String dniEstudiante;
    private String telefonoEstudiante;
    private String telefonoFijoEstudiante;
    private String legajoEstudiante;
    private String especialidadEstudiante;
    private String calleEstudiante;
    private Integer nroCalleEstudiante;
    private String barrioEstudiante;
    private String localidadEstudiante;
    private String provinciaEstudiante;

    // Campos calculados
    private Boolean esEditable;
    private LocalDateTime fechaActualizacion;
}
