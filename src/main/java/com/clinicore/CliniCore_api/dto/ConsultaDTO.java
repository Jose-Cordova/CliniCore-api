package com.clinicore.CliniCore_api.dto;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ConsultaDTO {


    private Integer id;

    private Integer doctorId; //el ID del doctor

    private String doctorNombre;

    private LocalDateTime fechaAtencion;

    private String tirajePa;

    private BigDecimal tirajeTemperatura;

    private BigDecimal tirajePeso;

    private BigDecimal tirajeEstatura;

    private String tirajeSintomas;

    private String diagnostico;

    private String tratamiento;

    private String nota;

    // Identificadores de relaciones necesarios para los flujos
    private Integer pacienteId;
    private String pacienteNombre;
    private Integer citaId;
}
