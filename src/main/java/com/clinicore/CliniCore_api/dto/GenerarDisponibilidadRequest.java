package com.clinicore.CliniCore_api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class GenerarDisponibilidadRequest {
    private Integer doctorId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}
