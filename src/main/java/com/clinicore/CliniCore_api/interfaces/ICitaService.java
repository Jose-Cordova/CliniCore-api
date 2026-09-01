package com.clinicore.CliniCore_api.interfaces;

import com.clinicore.CliniCore_api.dto.CitaDTO;
import com.clinicore.CliniCore_api.enums.EstadoCita;

import java.util.List;
import java.util.Optional;

public interface ICitaService {

    //obtenemos todas las citas
    List<CitaDTO> findAll();

    //Buscar una cita por su id
    CitaDTO  findById(Integer id);

    //Obtenemo el historial de citas asociadad a un paciente
    List<CitaDTO> findByPacienteId(Integer pacienteId);

    //Obtenemos las citas asinadas a un doctor
    List<CitaDTO> findByDoctorId(Integer doctorId);

    //Registra la cita que hacer un paciente
    CitaDTO agendarCita(CitaDTO requestDTO);

    //Cambiar el estado de una cita
    CitaDTO cambiarEstado(Integer id, EstadoCita nuevoEstado);

    //Para cancelar una cita, el paciente quiere cancelarla
    CitaDTO cancelarCita(Integer id);

    //Reasignar una cita, a el doctor de la misma espcialidad y a el mismo horario
    CitaDTO reasignarCita(Integer citaId, Integer nuevoHorarioId);
}
