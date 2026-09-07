package com.clinicore.CliniCore_api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PerfilDTO {
    private Integer id;
    private String email;
    private String tipo; // ADMIN, DOCTOR, PERSONAL

    // Para doctor
    private String nombre;
    private String apellido;
    private String telefono;
    private String codigo;
    private Integer especialidadId;
}