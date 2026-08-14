package com.clinicore.CliniCore_api.mappers;

import com.clinicore.CliniCore_api.dto.EspecialidadDTO;
import com.clinicore.CliniCore_api.entities.Especialidad;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EspecialidadMapper {
    EspecialidadDTO toDTO(Especialidad entity);
    Especialidad toEntity(EspecialidadDTO dto);
    List<EspecialidadDTO> toDtoList(List<Especialidad> entities);
}
