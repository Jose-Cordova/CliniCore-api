package com.clinicore.CliniCore_api.interfaces;

import com.clinicore.CliniCore_api.dto.DisponibilidadDTO;

import java.time.LocalDate;
import java.util.List;

public interface IDisponibilidadService {
    int generarDisponibilidadesPorRango(Integer doctorId, LocalDate fechaInicio, LocalDate fechaFin);
    List<DisponibilidadDTO> obtenerDisponiblesPorDoctorFecha(Integer doctorId, LocalDate fecha);
    List<DisponibilidadDTO> obtenerDisponiblesPorEspecialidadFecha(Integer especialidadId, LocalDate fecha);
}
