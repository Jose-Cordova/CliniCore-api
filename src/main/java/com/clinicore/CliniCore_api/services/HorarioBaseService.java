package com.clinicore.CliniCore_api.services;

import com.clinicore.CliniCore_api.dto.HorarioBaseDTO;
import com.clinicore.CliniCore_api.entities.Doctor;
import com.clinicore.CliniCore_api.entities.HorarioBase;
import com.clinicore.CliniCore_api.exceptions.BadRequestException;
import com.clinicore.CliniCore_api.exceptions.ResourceNotFoundException;
import com.clinicore.CliniCore_api.interfaces.IHorarioBaseService;
import com.clinicore.CliniCore_api.repository.DoctorRepository;
import com.clinicore.CliniCore_api.repository.HorarioBaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HorarioBaseService implements IHorarioBaseService {
    private final HorarioBaseRepository repository;
    private final DoctorRepository doctorRepository;

    @Override
    @Transactional(readOnly = true)
    public List<HorarioBaseDTO> findByDoctorId(Integer doctorId) {
        return repository.findByDoctorId(doctorId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public List<HorarioBaseDTO> guardarHorarios(Integer doctorId, List<HorarioBaseDTO> horarios) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(()-> new ResourceNotFoundException("Doctor no encontrado con ID: " + doctorId));

        //Eliminar horarios actuales del doctor y remplazar con los nuevos
        List<HorarioBase> actuales = repository.findByDoctorId(doctorId);
        repository.deleteAll(actuales);
        repository.flush();

        List<HorarioBase> nuevos = new ArrayList<>();
        for(HorarioBaseDTO dto : horarios){
            if(dto.getHoraInicio() == null || dto.getHoraFin() == null || dto.getDiaSemana() == null){
                throw new BadRequestException("Cada horario debe tener día, hora de inicio y hora de fin.");
            }
            if(!dto.getHoraInicio().isBefore(dto.getHoraFin())){
                throw new BadRequestException("La hora de inicio debe ser anterior a la hora de fin para el día " + dto.getDiaSemana());
            }

            HorarioBase horario = HorarioBase.builder()
                    .diaSemana(dto.getDiaSemana())
                    .horaInicio(dto.getHoraInicio())
                    .horaFin(dto.getHoraFin())
                    .horaAlmuerzoInicio(dto.getHoraAlmuerzoInicio())
                    .horaAlmuerzoFin(dto.getHoraAlmuerzoFin())
                    .doctor(doctor)
                    .build();
            nuevos.add(horario);
        }
        List<HorarioBase> guardados = repository.saveAll(nuevos);
        return guardados.stream().map(this::toDTO).toList();
    }

    private HorarioBaseDTO toDTO(HorarioBase entity){
        HorarioBaseDTO dto = new HorarioBaseDTO();
        dto.setId(entity.getId());
        dto.setDiaSemana(entity.getDiaSemana());
        dto.setHoraInicio(entity.getHoraInicio());
        dto.setHoraFin(entity.getHoraFin());
        dto.setHoraAlmuerzoInicio(entity.getHoraAlmuerzoInicio());
        dto.setHoraAlmuerzoFin(entity.getHoraAlmuerzoFin());
        dto.setDoctorId(entity.getDoctor().getId());
        return dto;
    }
}
