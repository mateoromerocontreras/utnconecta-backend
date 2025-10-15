package com.seminario.pasantias.persistence;

import com.seminario.pasantias.entity.Rol;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Optional;

@Mapper
public interface RolMapper {
    
    @Select("SELECT * FROM Rol WHERE activo = TRUE")
    @Results({
        @Result(property = "idRol", column = "id_rol"),
        @Result(property = "nombre", column = "nombre"),
        @Result(property = "descripcion", column = "descripcion"),
        @Result(property = "activo", column = "activo"),
        @Result(property = "fechaCreacion", column = "fecha_creacion")
    })
    List<Rol> findAllActive();
    
    @Select("SELECT * FROM Rol WHERE id_rol = #{id}")
    @Results({
        @Result(property = "idRol", column = "id_rol"),
        @Result(property = "nombre", column = "nombre"),
        @Result(property = "descripcion", column = "descripcion"),
        @Result(property = "activo", column = "activo"),
        @Result(property = "fechaCreacion", column = "fecha_creacion")
    })
    Optional<Rol> findById(@Param("id") Integer id);
    
    @Select("SELECT * FROM Rol WHERE nombre = #{nombre}")
    @Results({
        @Result(property = "idRol", column = "id_rol"),
        @Result(property = "nombre", column = "nombre"),
        @Result(property = "descripcion", column = "descripcion"),
        @Result(property = "activo", column = "activo"),
        @Result(property = "fechaCreacion", column = "fecha_creacion")
    })
    Optional<Rol> findByNombre(@Param("nombre") String nombre);
    
    @Insert("INSERT INTO Rol(nombre, descripcion, activo, fecha_creacion) VALUES(#{nombre}, #{descripcion}, #{activo}, #{fechaCreacion})")
    @Options(useGeneratedKeys = true, keyProperty = "idRol")
    void insert(Rol rol);
    
    @Update("UPDATE Rol SET nombre=#{nombre}, descripcion=#{descripcion} WHERE id_rol=#{idRol}")
    void update(Rol rol);
    
    @Update("UPDATE Rol SET activo = FALSE WHERE id_rol = #{id}")
    void deactivate(@Param("id") Integer id);
    
    @Delete("DELETE FROM Rol WHERE id_rol = #{id}")
    void delete(@Param("id") Integer id);
}