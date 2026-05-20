package com.seminario.pasantias.dto.response;

import com.seminario.pasantias.entity.EstadoPasantia;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO de respuesta detallada para Pasantía.
 * Incluye relaciones completas con Empresa, Carreras y Postulaciones.
 * Ideal para vista de detalle de una pasantía específica.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasantiaDetalleDTO {

    /**
     * ID único de la pasantía
     */
    private Integer idPasantia;

    /**
     * Título de la pasantía
     */
    private String titulo;

    /**
     * Puesto a cubrir
     */
    private String puestoACubrir;

    /**
     * Ciudad donde se realiza
     */
    private String ciudad;

    /**
     * Modalidad: Presencial, Híbrida, Remoto
     */
    private String modalidad;

    /**
     * Monto de asignación/estímulo
     */
    private Float asignacionEstimulo;

    /**
     * Cantidad total de pasantes solicitados
     */
    private Integer cantidadDePasantes;

    /**
     * Fecha de publicación
     */
    private LocalDate fechaPublicacion;

    /**
     * Fecha de caducidad
     */
    private LocalDate fechaCaducidad;

    /**
     * Estado actual de la pasantía
     */
    private EstadoPasantia estado;

    /**
     * Email de contacto
     */
    private String emailContacto;

    /**
     * Información de la empresa
     */
    private EmpresaSimpleDTO empresa;

    /**
     * Lista de carreras elegibles
     */
    private List<CarreraSimpleDTO> carreras;

    /**
     * Lista de postulaciones recibidas
     */
    private List<PostulacionSimpleDTO> postulaciones;

    /**
     * Estadísticas de postulaciones
     */
    private EstadisticasPostulacionDTO estadisticas;

    /**
     * Días restantes hasta la caducidad
     */
    private Long diasRestantes;

    /**
     * Indica si acepta nuevas postulaciones
     */
    private Boolean aceptaPostulaciones;

    /**
     * Conocimientos requeridos
     */
    private String conocimientos;

    /**
     * Otros requisitos
     */
    private String otrosRequisitos;

    /**
     * Beneficios ofrecidos
     */
    private String beneficios;

    /**
     * DTO simple para representar una Empresa
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EmpresaSimpleDTO {
        private Integer idEmpresa;
        private String nombre;
        private String cuit;
        private String email;
    }

    /**
     * DTO simple para representar una Carrera
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CarreraSimpleDTO {
        private Integer idCarrera;
        private String nombre;
        private String codigo;
    }

    /**
     * DTO simple para representar una Postulación en el contexto de Pasantía
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PostulacionSimpleDTO {
        private Integer idPostulacion;
        private LocalDate fechaPostulacion;
        private String estadoPostulacion;
        private Integer idEstudiante;
        private String nombreEstudiante;
    }

    /**
     * DTO con estadísticas de postulaciones
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EstadisticasPostulacionDTO {
        private Integer total;
        private Integer postulados;
        private Integer aceptados;
        private Integer rechazados;
        private Integer finalizados;
    }
}
