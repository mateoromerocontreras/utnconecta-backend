package com.seminario.pasantias.persistence;

import com.seminario.pasantias.entity.Postulacion;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface PostulacionMapper {

    @Select("""
        SELECT 
            id_postulacion AS idPostulacion,
            fecha_postulacion AS fecha,
            estado,
            estudiante_id AS idEstudiante,
            observaciones,
            fecha_actualizacion AS fechaActualizacion
        FROM postulacion
    """)
    List<Postulacion> findAll();

    @Select("""
        SELECT 
            id_postulacion AS idPostulacion,
            fecha_postulacion AS fecha,
            estado,
            estudiante_id AS idEstudiante,
            observaciones,
            fecha_actualizacion AS fechaActualizacion
        FROM postulacion
        WHERE id_postulacion = #{id}
    """)

    //@Select("SELECT COUNT(*) FROM postulacion WHERE estudiante_id = #{estudianteId} AND pasantia_id = #{pasantiaId}")
    //int countByEstudianteAndPasantia(@Param("estudianteId") Long estudianteId, @Param("pasantiaId") Long pasantiaId);

    Postulacion findById(@Param("id") Integer id);

    @Select("""
        SELECT COUNT(*) > 0 FROM postulacion
        WHERE estudiante_id = #{idEstudiante}
    """)
    boolean existsByEstudiante(@Param("idEstudiante") Integer idEstudiante);

    @Insert("""
        INSERT INTO postulacion (estudiante_id, fecha_postulacion, estado)
        VALUES (#{idEstudiante}, #{fecha}, #{estado})
    """)
    @Options(useGeneratedKeys = true, keyProperty = "idPostulacion", keyColumn = "id_postulacion")
    void insert(Postulacion postulacion);

    @Update("""
        UPDATE postulacion 
        SET estado = #{estado},
            observaciones = #{observaciones},
            fecha_actualizacion = #{fechaActualizacion}
        WHERE id_postulacion = #{idPostulacion}
    """)
    void update(Postulacion postulacion);
}
