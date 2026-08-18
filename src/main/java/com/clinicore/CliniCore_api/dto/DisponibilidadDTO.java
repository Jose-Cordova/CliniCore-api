package com.clinicore.CliniCore_api.dto;

import com.clinicore.CliniCore_api.enums.EstadoDisponibilidad;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class DisponibilidadDTO {
    private Long id;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private EstadoDisponibilidad estado;
    private Integer citaId; //Nulo si esta libre
    private Integer doctorId;
    private String doctorNombre; //Utill al paciente
}