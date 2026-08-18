package com.clinicore.CliniCore_api.dto;

import com.clinicore.CliniCore_api.enums.DiaSemana;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class HorarioBaseDTO {
    private Integer id;
    private DiaSemana diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private LocalTime horaAlmuerzoInicio;
    private LocalTime horaAlmuerzoFin;
    private Integer doctorId;
}