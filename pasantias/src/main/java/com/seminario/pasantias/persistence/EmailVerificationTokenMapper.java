package com.seminario.pasantias.persistence;

import com.seminario.pasantias.entity.EmailVerificationToken;
import org.apache.ibatis.annotations.*;

import java.util.Optional;

@Mapper
public interface EmailVerificationTokenMapper {

    @Insert("INSERT INTO EmailVerificationToken(id_usuario, token, fecha_expiracion, usado, fecha_creacion) " +
            "VALUES(#{idUsuario}, #{token}, #{fechaExpiracion}, #{usado}, #{fechaCreacion})")
    @Options(useGeneratedKeys = true, keyProperty = "idToken")
    void insert(EmailVerificationToken token);

    @Select("SELECT * FROM EmailVerificationToken WHERE token = #{token}")
    @Results({
            @Result(property = "idToken", column = "id_token"),
            @Result(property = "idUsuario", column = "id_usuario"),
            @Result(property = "fechaExpiracion", column = "fecha_expiracion"),
            @Result(property = "usado", column = "usado"),
            @Result(property = "fechaCreacion", column = "fecha_creacion")
    })
    Optional<EmailVerificationToken> findByToken(@Param("token") String token);

    @Update("UPDATE EmailVerificationToken SET usado = TRUE WHERE id_token = #{idToken}")
    void markAsUsed(@Param("idToken") Integer idToken);

    @Delete("DELETE FROM EmailVerificationToken WHERE id_usuario = #{idUsuario}")
    void deleteByUsuario(@Param("idUsuario") Integer idUsuario);
}
