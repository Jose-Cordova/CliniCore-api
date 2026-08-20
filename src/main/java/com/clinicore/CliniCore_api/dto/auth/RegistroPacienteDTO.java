package com.clinicore.CliniCore_api.dto.auth;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RegistroPacienteDTO {
    // Datos de acceso
    private String email;
    private String password;

    // Datos personales del paciente
    private String nombre;
    private String apellido;
    private String dui;
    private LocalDate fechaNacimiento;
    private String genero;
    private String direccion;
    private String telefono;
    private String alergiaIntolerancia; // opcional
}