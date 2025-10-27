package com.seminario.pasantias.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Empresa {
    private Integer idEmpresa;
    private String nombre;
    private String ciudad;
    private String calle;
    private Integer nroCalle;
    private String piso;
    private String departamento;
    private String barrio;
    private String email;
    private String cuit;
    private String razonSocial;
    private List<Contacto> contacto;
}
