package com.seminario.pasantias.persistence;

import com.seminario.pasantias.entity.Empresa;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface EmpresaMapper {
	@Select("SELECT * FROM Empresa")
	List<Empresa> findAll();

	@Select("SELECT * FROM Empresa WHERE id_empresa = #{id}")
	Empresa findById(@Param("id") Integer id);

	@Select("SELECT * FROM Empresa WHERE cuit = #{cuit}")
	Empresa findByCuit(@Param("cuit") String cuit);

	@Select("SELECT * FROM Empresa WHERE nombre = #{nombre}")
	List<Empresa> findByNombre(@Param("nombre") String nombre);

	@Insert("INSERT INTO Empresa(nombre, ciudad, direccion, email, cuit, razon_social) VALUES(#{nombre}, #{ciudad}, #{direccion}, #{email}, #{cuit}, #{razonSocial})")
	@Options(useGeneratedKeys = true, keyProperty = "idEmpresa")
	void insert(Empresa empresa);

	@Update("UPDATE Empresa SET nombre=#{nombre}, ciudad=#{ciudad}, direccion=#{direccion}, email=#{email}, cuit=#{cuit}, razon_social=#{razonSocial} WHERE id_empresa=#{idEmpresa}")
	void update(Empresa empresa);

	@Delete("DELETE FROM Empresa WHERE id_empresa = #{id}")
	void delete(@Param("id") Integer id);

	@Delete("DELETE FROM Empresa WHERE cuit = #{cuit}")
	void deleteByCuit(@Param("cuit") String cuit);
}
