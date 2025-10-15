package com.seminario.pasantias.persistence;

import com.seminario.pasantias.entity.Usuario;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Optional;

@Mapper
public interface UsuarioMapper {
    
    @Select("SELECT u.*, r.nombre as rol_nombre, r.descripcion as rol_descripcion " +
            "FROM Usuario u LEFT JOIN Rol r ON u.id_rol = r.id_rol " +
            "WHERE u.id_usuario = #{id}")
    @Results({
        @Result(property = "idUsuario", column = "id_usuario"),
        @Result(property = "idRol", column = "id_rol"),
        @Result(property = "rol.idRol", column = "id_rol"),
        @Result(property = "rol.nombre", column = "rol_nombre"),
        @Result(property = "rol.descripcion", column = "rol_descripcion")
    })
    Optional<Usuario> findById(@Param("id") Integer id);
    
    @Select("SELECT u.*, r.nombre as rol_nombre, r.descripcion as rol_descripcion " +
            "FROM Usuario u LEFT JOIN Rol r ON u.id_rol = r.id_rol " +
            "WHERE u.username = #{username}")
    @Results({
        @Result(property = "idUsuario", column = "id_usuario"),
        @Result(property = "idRol", column = "id_rol"),
        @Result(property = "rol.idRol", column = "id_rol"),
        @Result(property = "rol.nombre", column = "rol_nombre"),
        @Result(property = "rol.descripcion", column = "rol_descripcion")
    })
    Optional<Usuario> findByUsername(@Param("username") String username);
    
    @Select("SELECT u.*, r.nombre as rol_nombre, r.descripcion as rol_descripcion " +
            "FROM Usuario u LEFT JOIN Rol r ON u.id_rol = r.id_rol " +
            "WHERE u.email = #{email}")
    @Results({
        @Result(property = "idUsuario", column = "id_usuario"),
        @Result(property = "idRol", column = "id_rol"),
        @Result(property = "rol.idRol", column = "id_rol"),
        @Result(property = "rol.nombre", column = "rol_nombre"),
        @Result(property = "rol.descripcion", column = "rol_descripcion")
    })
    Optional<Usuario> findByEmail(@Param("email") String email);
    
    @Select("SELECT u.*, r.nombre as rol_nombre, r.descripcion as rol_descripcion " +
            "FROM Usuario u LEFT JOIN Rol r ON u.id_rol = r.id_rol " +
            "WHERE u.activo = TRUE")
    @Results({
        @Result(property = "idUsuario", column = "id_usuario"),
        @Result(property = "idRol", column = "id_rol"),
        @Result(property = "rol.idRol", column = "id_rol"),
        @Result(property = "rol.nombre", column = "rol_nombre"),
        @Result(property = "rol.descripcion", column = "rol_descripcion")
    })
    List<Usuario> findAllActive();
    
    @Insert("INSERT INTO Usuario(username, email, password, id_rol, activo, fecha_creacion) " +
            "VALUES(#{username}, #{email}, #{password}, #{idRol}, #{activo}, #{fechaCreacion})")
    @Options(useGeneratedKeys = true, keyProperty = "idUsuario")
    void insert(Usuario usuario);
    
    @Update("UPDATE Usuario SET username=#{username}, email=#{email}, password=#{password}, " +
            "id_rol=#{idRol}, activo=#{activo} WHERE id_usuario=#{idUsuario}")
    void update(Usuario usuario);
    
    @Update("UPDATE Usuario SET activo = FALSE WHERE id_usuario = #{id}")
    void deactivate(@Param("id") Integer id);
    
    @Delete("DELETE FROM Usuario WHERE id_usuario = #{id}")
    void delete(@Param("id") Integer id);
    
    @Delete("DELETE FROM Usuario WHERE username = #{username}")
    void deleteByUsername(@Param("username") String username);
}