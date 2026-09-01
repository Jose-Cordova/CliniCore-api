package com.clinicore.CliniCore_api.mappers;


import com.clinicore.CliniCore_api.dto.CitaDTO;
import com.clinicore.CliniCore_api.entities.Cita;
import com.clinicore.CliniCore_api.entities.Disponibilidad;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CitaMapper {


    @Mapping(source = "cita.id", target = "id")
    @Mapping(source = "cita.motivo", target = "motivo")
    @Mapping(source = "cita.estado", target = "estado")
    @Mapping(source = "cita.paciente.id", target = "pacienteId")
    @Mapping(source = "cita.paciente.nombre", target = "pacienteNombre")
    @Mapping(source = "cita.consulta.id", target = "consultaId")
    @Mapping(source = "disponibilidad.fecha", target = "fecha")
    @Mapping(source = "disponibilidad.horaInicio", target = "horaInicio")
    @Mapping(source = "disponibilidad.doctor.id", target = "doctorId")
    @Mapping(source = "disponibilidad.doctor.nombre", target = "doctorNombre")
    @Mapping(source = "disponibilidad.doctor.especialidad.nombre", target = "especialidadNombre")
    // se quita si se quiere la disponibilidad del lado del cliente
    @Mapping(source = "disponibilidad.id", target = "disponibilidadId")
    CitaDTO toDTO(Cita cita, Disponibilidad disponibilidad);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "paciente", ignore = true)
    @Mapping(target = "consulta", ignore = true)
    Cita toEntity(CitaDTO citaDTO);

}
