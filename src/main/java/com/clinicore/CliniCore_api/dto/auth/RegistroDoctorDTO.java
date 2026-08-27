package com.clinicore.CliniCore_api.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistroDoctorDTO {
    // Datos de acceso
    private String email;
    private String password;

    // Datos del doctor
    private String nombre;
    private String apellido;
    private String telefono;
    private String codigo;          // número de colegiado
    private Integer especialidadId; // FK a especialidad
}