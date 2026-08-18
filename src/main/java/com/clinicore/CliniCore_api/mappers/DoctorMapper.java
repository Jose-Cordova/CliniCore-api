package com.clinicore.CliniCore_api.mappers;

import com.clinicore.CliniCore_api.dto.DoctorDTO;
import com.clinicore.CliniCore_api.entities.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DoctorMapper {
    @Mapping(target = "usuarioId", source = "usuario.id")
    @Mapping(target = "especialidadId", source = "especialidad.id")
    DoctorDTO toDTO(Doctor entity);

    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "especialidad", ignore = true)
    Doctor toEntity(DoctorDTO dto);

    List<DoctorDTO> toDtoList(List<Doctor> entities);
}
