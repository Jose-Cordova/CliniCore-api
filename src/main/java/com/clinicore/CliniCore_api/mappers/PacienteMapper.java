package com.clinicore.CliniCore_api.mappers;

import com.clinicore.CliniCore_api.dto.PacienteDTO;
import com.clinicore.CliniCore_api.entities.Paciente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PacienteMapper {

    //Convertimos la entidad Paciente a PacienteDTO
    @Mapping(target = "usuarioId", source = "usuario.id")
    PacienteDTO toDTO(Paciente entity);

    // Convierte de PacienteDTO a Entidad Paciente
    @Mapping(target = "usuario", ignore = true)
    Paciente toEntity(PacienteDTO dto);

    // Convierte una lista de Pacientes a una lista de DTOs
    List<PacienteDTO> toDtoList(List<Paciente> entities);


}
