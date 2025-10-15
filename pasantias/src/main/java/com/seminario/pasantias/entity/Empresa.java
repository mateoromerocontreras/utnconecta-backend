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
    private String direccion;
    private String email;
    private String cuit;
    private String razonSocial;
    private List<Contacto> contacto;
}
