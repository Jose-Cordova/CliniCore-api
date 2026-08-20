package com.clinicore.CliniCore_api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorDTO {
    private Integer id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String codigo;
    private Integer usuarioId;
    private Integer especialidadId;
}
