package com.seminario.pasantias.persistence;

import com.seminario.pasantias.entity.Estudiante;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Optional;

@Mapper
public interface EstudianteMapper {
    
    @Select("SELECT e.*, u.username, u.email as usuario_email, u.activo as usuario_activo " +
            "FROM Estudiante e LEFT JOIN Usuario u ON e.id_usuario = u.id_usuario " +
            "WHERE e.id_estudiante = #{id}")
    @Results({
        @Result(property = "idEstudiante", column = "id_estudiante"),
        @Result(property = "nroCalle", column = "nro_calle"),
        @Result(property = "nroLegajo", column = "nro_legajo"),
        @Result(property = "telCelular", column = "tel_celular"),
        @Result(property = "telFijo", column = "tel_fijo"),
        @Result(property = "idUsuario", column = "id_usuario"),
        @Result(property = "fechaCreacion", column = "fecha_creacion"),
        @Result(property = "usuario.idUsuario", column = "id_usuario"),
        @Result(property = "usuario.username", column = "username"),
        @Result(property = "usuario.email", column = "usuario_email"),
        @Result(property = "usuario.activo", column = "usuario_activo")
    })
    Optional<Estudiante> findById(@Param("id") Integer id);
    
    @Select("SELECT e.*, u.username, u.email as usuario_email, u.activo as usuario_activo " +
            "FROM Estudiante e LEFT JOIN Usuario u ON e.id_usuario = u.id_usuario " +
            "WHERE e.email = #{email}")
    @Results({
        @Result(property = "idEstudiante", column = "id_estudiante"),
        @Result(property = "nroCalle", column = "nro_calle"),
        @Result(property = "nroLegajo", column = "nro_legajo"),
        @Result(property = "telCelular", column = "tel_celular"),
        @Result(property = "telFijo", column = "tel_fijo"),
        @Result(property = "idUsuario", column = "id_usuario"),
        @Result(property = "fechaCreacion", column = "fecha_creacion"),
        @Result(property = "usuario.idUsuario", column = "id_usuario"),
        @Result(property = "usuario.username", column = "username"),
        @Result(property = "usuario.email", column = "usuario_email"),
        @Result(property = "usuario.activo", column = "usuario_activo")
    })
    Optional<Estudiante> findByEmail(@Param("email") String email);
    
    @Select("SELECT e.*, u.username, u.email as usuario_email, u.activo as usuario_activo " +
            "FROM Estudiante e LEFT JOIN Usuario u ON e.id_usuario = u.id_usuario " +
            "WHERE e.id_usuario = #{idUsuario} " +
            "ORDER BY e.fecha_creacion DESC, e.id_estudiante DESC " +
            "LIMIT 1")
    @Results({
        @Result(property = "idEstudiante", column = "id_estudiante"),
        @Result(property = "nroCalle", column = "nro_calle"),
        @Result(property = "nroLegajo", column = "nro_legajo"),
        @Result(property = "telCelular", column = "tel_celular"),
        @Result(property = "telFijo", column = "tel_fijo"),
        @Result(property = "idUsuario", column = "id_usuario"),
        @Result(property = "fechaCreacion", column = "fecha_creacion"),
        @Result(property = "usuario.idUsuario", column = "id_usuario"),
        @Result(property = "usuario.username", column = "username"),
        @Result(property = "usuario.email", column = "usuario_email"),
        @Result(property = "usuario.activo", column = "usuario_activo")
    })
    Optional<Estudiante> findByUsuarioId(@Param("idUsuario") Integer idUsuario);
    
    @Select("SELECT e.*, u.username, u.email as usuario_email, u.activo as usuario_activo " +
            "FROM Estudiante e LEFT JOIN Usuario u ON e.id_usuario = u.id_usuario " +
            "WHERE e.activo = TRUE")
    @Results({
        @Result(property = "idEstudiante", column = "id_estudiante"),
        @Result(property = "nroCalle", column = "nro_calle"),
        @Result(property = "nroLegajo", column = "nro_legajo"),
        @Result(property = "telCelular", column = "tel_celular"),
        @Result(property = "telFijo", column = "tel_fijo"),
        @Result(property = "idUsuario", column = "id_usuario"),
        @Result(property = "fechaCreacion", column = "fecha_creacion"),
        @Result(property = "usuario.idUsuario", column = "id_usuario"),
        @Result(property = "usuario.username", column = "username"),
        @Result(property = "usuario.email", column = "usuario_email"),
        @Result(property = "usuario.activo", column = "usuario_activo")
    })
    List<Estudiante> findAllActive();
    
    @Select("SELECT e.*, u.username, u.email as usuario_email, u.activo as usuario_activo " +
            "FROM Estudiante e LEFT JOIN Usuario u ON e.id_usuario = u.id_usuario " +
            "WHERE e.nombre = #{nombre} AND e.activo = TRUE")
    @Results({
        @Result(property = "idEstudiante", column = "id_estudiante"),
        @Result(property = "nroCalle", column = "nro_calle"),
        @Result(property = "nroLegajo", column = "nro_legajo"),
        @Result(property = "telCelular", column = "tel_celular"),
        @Result(property = "telFijo", column = "tel_fijo"),
        @Result(property = "idUsuario", column = "id_usuario"),
        @Result(property = "fechaCreacion", column = "fecha_creacion"),
        @Result(property = "usuario.idUsuario", column = "id_usuario"),
        @Result(property = "usuario.username", column = "username"),
        @Result(property = "usuario.email", column = "usuario_email"),
        @Result(property = "usuario.activo", column = "usuario_activo")
    })
    Optional<Estudiante> findByNombre(@Param("nombre") String nombre);
    
    @Insert("INSERT INTO Estudiante(dni, apellido, nombre, especialidad, nro_legajo, calle, nro_calle, " +
            "barrio, localidad, provincia, email, tel_celular, tel_fijo, id_usuario, activo, fecha_creacion) " +
            "VALUES(#{dni}, #{apellido}, #{nombre}, #{especialidad}, #{nroLegajo}, #{calle}, #{nroCalle}, " +
            "#{barrio}, #{localidad}, #{provincia}, #{email}, #{telCelular}, #{telFijo}, #{idUsuario}, #{activo}, #{fechaCreacion})")
    @Options(useGeneratedKeys = true, keyProperty = "idEstudiante")
    void insert(Estudiante estudiante);
    
    @Update("UPDATE Estudiante SET dni=#{dni}, apellido=#{apellido}, nombre=#{nombre}, " +
            "especialidad=#{especialidad}, nro_legajo=#{nroLegajo}, calle=#{calle}, nro_calle=#{nroCalle}, " +
            "barrio=#{barrio}, localidad=#{localidad}, provincia=#{provincia}, email=#{email}, " +
            "tel_celular=#{telCelular}, tel_fijo=#{telFijo}, activo=#{activo} WHERE id_estudiante=#{idEstudiante}")
    void update(Estudiante estudiante);
    
    @Update("UPDATE Estudiante SET activo = FALSE WHERE id_estudiante = #{id}")
    void deactivate(@Param("id") Integer id);
    
    @Delete("DELETE FROM Estudiante WHERE id_estudiante = #{id}")
    void delete(@Param("id") Integer id);
}