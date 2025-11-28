package com.seminario.pasantias.dto;

import com.seminario.pasantias.entity.Contacto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaRequest {
	private String nombre;
	private String razonSocial;
	private String cuit;
	private String ciudad;
	private String calle;
	private Integer nroCalle;
	private String piso;
	private String departamento;
	private String barrio;
	private String email;
	private List<Contacto> contacto;
}
