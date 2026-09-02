package com.clinicore.CliniCore_api.interfaces;

import com.clinicore.CliniCore_api.dto.HorarioBaseDTO;

import java.util.List;

public interface IHorarioBaseService {
    //Obtener horarios de un doctor
    List<HorarioBaseDTO> findByDoctorId(Integer doctorId);
    //Guardar/Actualizar los horarios de un doctor
    List<HorarioBaseDTO> guardarHorarios(Integer doctorId, List<HorarioBaseDTO> horarios);
}
