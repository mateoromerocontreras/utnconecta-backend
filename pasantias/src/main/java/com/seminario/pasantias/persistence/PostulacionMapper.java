package com.seminario.pasantias.persistence;

import com.seminario.pasantias.dto.request.PostulacionFiltroDTO;
import com.seminario.pasantias.entity.Postulacion;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Optional;

/**
 * Mapper de MyBatis para la entidad Postulacion.
 * Define operaciones CRUD y consultas personalizadas.
 */
@Mapper
public interface PostulacionMapper {

    /**
     * Buscar una postulación por su ID.
     */
    @Select("""
        SELECT 
            po.id_postulacion AS idPostulacion,
            po.fecha_postulacion AS fechaPostulacion,
            po.fecha_inicio_contrato AS fechaInicioContrato,
            po.duracion_meses AS duracionMeses,
            po.estado,
            po.id_pasantia AS idPasantia,
            po.id_estudiante AS idEstudiante
        FROM Postulacion po
        WHERE po.id_postulacion = #{id}
    """)
    @Results(id = "postulacionResult", value = {
        @Result(property = "idPostulacion", column = "idPostulacion"),
        @Result(property = "fechaPostulacion", column = "fechaPostulacion"),
        @Result(property = "fechaInicioContrato", column = "fechaInicioContrato"),
        @Result(property = "duracionMeses", column = "duracionMeses")
    })
    Optional<Postulacion> findById(@Param("id") Integer id);

    /**
     * Buscar postulación completa con todas sus relaciones.
     */
    @Select("""
        SELECT 
            po.id_postulacion AS idPostulacion,
            po.fecha_postulacion AS fechaPostulacion,
            po.fecha_inicio_contrato AS fechaInicioContrato,
            po.duracion_meses AS duracionMeses,
            po.estado,
            po.id_pasantia AS idPasantia,
            po.id_estudiante AS idEstudiante
        FROM Postulacion po
        WHERE po.id_postulacion = #{id}
    """)
    @Results({
        @Result(property = "idPostulacion", column = "idPostulacion"),
        @Result(property = "pasantia", column = "idPasantia", 
                one = @One(select = "com.seminario.pasantias.persistence.PasantiaMapper.findById")),
        @Result(property = "estudiante", column = "idEstudiante", 
                one = @One(select = "com.seminario.pasantias.persistence.EstudianteMapper.findById"))
    })
    Optional<Postulacion> findByIdWithRelations(@Param("id") Integer id);

    /**
     * Listar todas las postulaciones.
     */
    @Select("""
        SELECT 
            po.id_postulacion AS idPostulacion,
            po.fecha_postulacion AS fechaPostulacion,
            po.fecha_inicio_contrato AS fechaInicioContrato,
            po.duracion_meses AS duracionMeses,
            po.estado,
            po.id_pasantia AS idPasantia,
            po.id_estudiante AS idEstudiante
        FROM Postulacion po
        ORDER BY po.fecha_postulacion DESC
    """)
    @ResultMap("postulacionResult")
    List<Postulacion> findAll();

    /**
     * Buscar postulaciones por estudiante.
     */
    @Select("""
        SELECT 
            po.id_postulacion AS idPostulacion,
            po.fecha_postulacion AS fechaPostulacion,
            po.fecha_inicio_contrato AS fechaInicioContrato,
            po.duracion_meses AS duracionMeses,
            po.estado,
            po.id_pasantia AS idPasantia,
            po.id_estudiante AS idEstudiante
        FROM Postulacion po
        WHERE po.id_estudiante = #{idEstudiante}
        ORDER BY po.fecha_postulacion DESC
    """)
    @ResultMap("postulacionResult")
    List<Postulacion> findByEstudiante(@Param("idEstudiante") Integer idEstudiante);

    /**
     * Buscar postulaciones por pasantía.
     */
    @Select("""
        SELECT 
            po.id_postulacion AS idPostulacion,
            po.fecha_postulacion AS fechaPostulacion,
            po.fecha_inicio_contrato AS fechaInicioContrato,
            po.duracion_meses AS duracionMeses,
            po.estado,
            po.id_pasantia AS idPasantia,
            po.id_estudiante AS idEstudiante
        FROM Postulacion po
        WHERE po.id_pasantia = #{idPasantia}
        ORDER BY po.fecha_postulacion DESC
    """)
    @ResultMap("postulacionResult")
    List<Postulacion> findByPasantiaId(@Param("idPasantia") Integer idPasantia);

    /**
     * Buscar postulaciones por estado.
     */
    @Select("""
        SELECT 
            po.id_postulacion AS idPostulacion,
            po.fecha_postulacion AS fechaPostulacion,
            po.fecha_inicio_contrato AS fechaInicioContrato,
            po.duracion_meses AS duracionMeses,
            po.estado,
            po.id_pasantia AS idPasantia,
            po.id_estudiante AS idEstudiante
        FROM Postulacion po
        WHERE po.estado = #{estado}
        ORDER BY po.fecha_postulacion DESC
    """)
    @ResultMap("postulacionResult")
    List<Postulacion> findByEstado(@Param("estado") String estado);

    /**
     * Buscar postulaciones activas de un estudiante (no finalizadas).
     */
    @Select("""
        SELECT 
            po.id_postulacion AS idPostulacion,
            po.fecha_postulacion AS fechaPostulacion,
            po.fecha_inicio_contrato AS fechaInicioContrato,
            po.duracion_meses AS duracionMeses,
            po.estado,
            po.id_pasantia AS idPasantia,
            po.id_estudiante AS idEstudiante
        FROM Postulacion po
        WHERE po.id_estudiante = #{idEstudiante}
        AND po.estado != 'FINALIZADA'
        ORDER BY po.fecha_postulacion DESC
    """)
    @ResultMap("postulacionResult")
    List<Postulacion> findActiveByEstudiante(@Param("idEstudiante") Integer idEstudiante);

    /**
     * Buscar postulaciones con filtros dinámicos.
     * Se implementa en XML para mayor flexibilidad.
     */
    List<Postulacion> findWithFilters(PostulacionFiltroDTO filtro);

    /**
     * Contar total de postulaciones con filtros.
     */
    Long countWithFilters(PostulacionFiltroDTO filtro);

    /**
     * Verificar si un estudiante ya postuló a una pasantía.
     */
    @Select("""
        SELECT COUNT(*) > 0 
        FROM Postulacion
        WHERE id_estudiante = #{idEstudiante}
        AND id_pasantia = #{idPasantia}
    """)
    boolean existsByEstudianteAndPasantia(@Param("idEstudiante") Integer idEstudiante, 
                                          @Param("idPasantia") Integer idPasantia);

    /**
     * Verificar si existe una postulación.
     */
    @Select("SELECT COUNT(*) > 0 FROM Postulacion WHERE id_postulacion = #{id}")
    boolean existsById(@Param("id") Integer id);

    /**
     * Insertar una nueva postulación.
     */
    @Insert("""
        INSERT INTO Postulacion (
            fecha_postulacion, fecha_inicio_contrato, duracion_meses,
            estado, id_pasantia, id_estudiante
        ) VALUES (
            #{fechaPostulacion}, #{fechaInicioContrato}, #{duracionMeses},
            #{estado}, #{pasantia.idPasantia}, #{estudiante.idEstudiante}
        )
    """)
    @Options(useGeneratedKeys = true, keyProperty = "idPostulacion", keyColumn = "id_postulacion")
    void insert(Postulacion postulacion);

    /**
     * Actualizar una postulación existente.
     */
    @Update("""
        UPDATE Postulacion SET
            fecha_postulacion = #{fechaPostulacion},
            fecha_inicio_contrato = #{fechaInicioContrato},
            duracion_meses = #{duracionMeses},
            estado = #{estado}
        WHERE id_postulacion = #{idPostulacion}
    """)
    void update(Postulacion postulacion);

    /**
     * Actualizar solo el estado de una postulación.
     */
    @Update("""
        UPDATE Postulacion 
        SET estado = #{estado}
        WHERE id_postulacion = #{idPostulacion}
    """)
    void updateEstado(@Param("idPostulacion") Integer idPostulacion, @Param("estado") String estado);

    /**
     * Actualizar datos del contrato (fecha inicio y duración).
     */
    @Update("""
        UPDATE Postulacion 
        SET fecha_inicio_contrato = #{fechaInicioContrato},
            duracion_meses = #{duracionMeses}
        WHERE id_postulacion = #{idPostulacion}
    """)
    void updateContrato(@Param("idPostulacion") Integer idPostulacion, 
                        @Param("fechaInicioContrato") String fechaInicioContrato,
                        @Param("duracionMeses") Integer duracionMeses);

    /**
     * Eliminar una postulación.
     */
    @Delete("DELETE FROM Postulacion WHERE id_postulacion = #{id}")
    void delete(@Param("id") Integer id);

    /**
     * Contar postulaciones por estudiante.
     */
    @Select("SELECT COUNT(*) FROM Postulacion WHERE id_estudiante = #{idEstudiante}")
    Integer countByEstudiante(@Param("idEstudiante") Integer idEstudiante);

    /**
     * Contar postulaciones por pasantía.
     */
    @Select("SELECT COUNT(*) FROM Postulacion WHERE id_pasantia = #{idPasantia}")
    Integer countByPasantia(@Param("idPasantia") Integer idPasantia);
    
    /**
     * Contar postulaciones por pasantía excluyendo un estado específico.
     */
    @Select("""
        SELECT COUNT(*) 
        FROM Postulacion 
        WHERE id_pasantia = #{idPasantia} 
        AND estado != #{estado}
    """)
    Integer countByPasantiaIdAndEstadoNot(@Param("idPasantia") Integer idPasantia, 
                                           @Param("estado") String estado);
}
