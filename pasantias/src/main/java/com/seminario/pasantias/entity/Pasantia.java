package com.seminario.pasantias.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pasantia {
    private Integer idPasantia;
    
    // Relación con Empresa
    private Integer idEmpresa;
    private String nombreEmpresa;
    
    // Ubicación
    private String ciudad;
    private String calle;
    private Integer nroCalle;
    private String piso;
    private String departamento;
    private String barrio;
    private String lugarTrabajo; // Dirección completa formateada
    
    // Modalidad
    private String modalidad; // Presencial, Híbrida, Remoto
    
    // Referente de RRHH
    private String referenteRRHH;
    private String horarioEntrevista;
    
    // Carreras y Requisitos (Relación muchos a muchos)
    private List<Integer> idsCarreras; // Lista de IDs de carreras relacionadas
    private List<Carrera> carreras; // Lista de objetos Carrera completos
    private String conocimientos; // Texto libre con requisitos y conocimientos
    private String aptitudesPerfilDeseado;
    private String otrosRequisitos;
    
    // Condiciones económicas y horario
    private BigDecimal asignacionEstimulo; // Monto del estímulo
    private String horarioTrabajo;
    
    // Descripción del puesto
    private String puestoArea;
    private String principalesResponsabilidades; // Texto con lista de responsabilidades
    private String beneficios; // Texto con lista de beneficios
    
    // Cantidad y estado
    private Integer cantidadPasantes;
    private Integer vacantesDisponibles;
    private EstadoPasantia estado; // Enum: DRAFT, PENDIENTE_APROBACION, PUBLICADA, etc.
    
    // Contacto
    private String emailContacto;
    private String numeroResolucion; // Ej: A.R.M. 161/25
    
    // Metadata
    private LocalDateTime fechaPublicacion;
    private LocalDateTime fechaCierre;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
