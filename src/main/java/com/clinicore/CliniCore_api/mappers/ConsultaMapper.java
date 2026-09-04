package com.clinicore.CliniCore_api.mappers;


import com.clinicore.CliniCore_api.dto.ConsultaDTO;
import com.clinicore.CliniCore_api.entities.Consulta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConsultaMapper {

    @Mapping(source = "consulta.id", target = "id")
    @Mapping(source = "consulta.paciente.id", target = "pacienteId")
    @Mapping(source = "consulta.paciente.nombre", target = "pacienteNombre")
    @Mapping(source = "consulta.doctor.id", target = "doctorId")
    @Mapping(source = "consulta.doctor.nombre", target = "doctorNombre")
    @Mapping(source = "citaId", target = "citaId")
    ConsultaDTO toDTO(Consulta consulta, Integer citaId);

    @Mapping(target = "id", ignore = true)
    //esta Protegida para asignación automática en Service
    @Mapping(target = "fechaAtencion", ignore = true)
    @Mapping(target = "paciente", ignore = true)
    @Mapping(target = "doctor", ignore = true) //nuevo agregado
    Consulta toEntity(ConsultaDTO consultaDTO);

}
