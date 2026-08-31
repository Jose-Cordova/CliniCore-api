package com.clinicore.CliniCore_api.interfaces;

import com.clinicore.CliniCore_api.dto.ConsultaDTO;

import java.util.List;

public interface IConsultaService {


    // la emfermera: Inicia la  consulta tomando signos vitales
    ConsultaDTO registrarTiraje(ConsultaDTO triajeDTO);

    //  el doctor Completa la consulta con diagnóstico y receta desde la cita
    ConsultaDTO finalizarConsultaDoctor(Integer citaId, ConsultaDTO consultaDTO);

    // la emfermera Modifica únicamente los signos vitales si hubo un error
    ConsultaDTO editarTirajeEnfermera(Integer id, ConsultaDTO triajeDTO);

    // El doctor Modifica únicamente los campos de diagnóstico/receta
    ConsultaDTO editarConsultaDoctor(Integer id, ConsultaDTO consultaDTO);

    //  Obtener consultas por paciente para vista general
    List<ConsultaDTO> findByPacienteId(Integer pacienteId);

    // Obtener consultas realizadas por el médico vista del lado del medico
    List<ConsultaDTO> findByDoctorId(Integer doctorId);
}

