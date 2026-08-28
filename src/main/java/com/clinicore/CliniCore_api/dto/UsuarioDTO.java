package com.clinicore.CliniCore_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UsuarioDTO {
    private Integer id;
    private String email;
    private boolean estado;
    private String rol;
    private String nombre;
    private String tipo; // DOCTOR, PACIENTE, ADMIN, PERSONAL
}