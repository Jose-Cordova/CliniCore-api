package com.clinicore.CliniCore_api.interfaces;

import com.clinicore.CliniCore_api.dto.PacienteDTO;

import java.util.List;

public interface IPacienteService {
    // Método para obtener la lista de todos los pacientes
    List<PacienteDTO> findAll();

    // Método para obtener un solo paciente mediante su ID numérico
    PacienteDTO findById(Integer id);

    // Método para actualizar un paciente existente
    PacienteDTO update(PacienteDTO dto);

    // Método para cambiar el estado de archivado de un paciente
    void cambiarEstadoArchivado(Integer id, boolean archivado);

    // Método para buscar un paciente usando su número de expediente
    PacienteDTO findByCodigoExpediente(String codigoExpediente);
}

