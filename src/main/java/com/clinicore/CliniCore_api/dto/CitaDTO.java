package com.clinicore.CliniCore_api.dto;

import com.clinicore.CliniCore_api.enums.EstadoCita;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class CitaDTO {
    private Integer id;

    private Integer disponibilidadId;

    private LocalDate fecha; //Sale de la Disponibilidad

    private LocalTime horaInicio; //Sale de la Disponibilidad

    private String motivo;

    private EstadoCita estado;

    private Integer pacienteId;

    private String pacienteNombre;

    private Integer doctorId; //Sale de la Disponibilidad

    private String doctorNombre;

    private String especialidadNombre;

    private Integer consultaId; //Nulo hasta que la enfermera tome los signos


}