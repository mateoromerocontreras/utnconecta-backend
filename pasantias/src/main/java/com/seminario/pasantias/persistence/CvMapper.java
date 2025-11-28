package com.seminario.pasantias.persistence;

import com.seminario.pasantias.entity.Cv;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CvMapper {

    @Insert("INSERT INTO CV(nombre_archivo, datos_cv, id_estudiante) VALUES(#{nombreArchivo}, #{datosCv}, #{idEstudiante})")
    @Options(useGeneratedKeys = true, keyProperty = "idCv")
    void insert(Cv cv);

    @Select("SELECT id_cv, nombre_archivo, fecha_subida, id_estudiante FROM CV WHERE id_cv = #{idCv}")
    Optional<Cv> findById(@Param("idCv") Integer idCv);

    @Select("SELECT id_cv, nombre_archivo, datos_cv, fecha_subida, id_estudiante FROM CV WHERE id_cv = #{idCv}")
    Optional<Cv> findWithDataById(@Param("idCv") Integer idCv);

    @Select("SELECT id_cv, nombre_archivo, fecha_subida, id_estudiante FROM CV WHERE id_estudiante = #{idEstudiante}")
    List<Cv> findByEstudianteId(@Param("idEstudiante") Integer idEstudiante);

    @Delete("DELETE FROM CV WHERE id_cv = #{idCv}")
    void delete(@Param("idCv") Integer idCv);
}
