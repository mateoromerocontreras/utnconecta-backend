package com.seminario.pasantias.persistence;

import com.seminario.pasantias.entity.Contacto;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ContactoMapper {
    @Select("SELECT * FROM Contacto WHERE id_empresa = #{idEmpresa}")
    List<Contacto> findByEmpresaId(@Param("idEmpresa") Integer idEmpresa);

    @Insert("INSERT INTO Contacto(nombre, apellido, email_responsable, telefono_responsable, id_empresa) VALUES(#{nombre}, #{apellido}, #{emailResponsable}, #{telefonoResponsable}, #{idEmpresa})")
    @Options(useGeneratedKeys = true, keyProperty = "idContacto")
    void insert(Contacto contacto);

    @Update("UPDATE Contacto SET nombre=#{nombre}, apellido=#{apellido}, email_responsable=#{emailResponsable}, telefono_responsable=#{telefonoResponsable} WHERE id_contacto=#{idContacto}")
    void update(Contacto contacto);

    @Delete("DELETE FROM Contacto WHERE id_empresa = #{idEmpresa}")
    void deleteByEmpresaId(@Param("idEmpresa") Integer idEmpresa);
}