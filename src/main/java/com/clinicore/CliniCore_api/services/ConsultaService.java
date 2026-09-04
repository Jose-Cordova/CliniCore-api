package com.clinicore.CliniCore_api.services;


import com.clinicore.CliniCore_api.dto.ConsultaDTO;
import com.clinicore.CliniCore_api.entities.Cita;
import com.clinicore.CliniCore_api.entities.Consulta;
import com.clinicore.CliniCore_api.enums.EstadoCita;
import com.clinicore.CliniCore_api.exceptions.BadRequestException;
import com.clinicore.CliniCore_api.exceptions.ResourceNotFoundException;
import com.clinicore.CliniCore_api.interfaces.IConsultaService;
import com.clinicore.CliniCore_api.mappers.ConsultaMapper;
import com.clinicore.CliniCore_api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultaService implements IConsultaService {

    private final ConsultaRepository consultaRepository;
    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final DisponibilidadRepository disponibilidadRepository;
    private final DoctorRepository doctorRepository;
    private final ConsultaMapper consultaMapper;


    @Override
    @Transactional
    public ConsultaDTO registrarTiraje(ConsultaDTO tirajeDTO) {
        if (tirajeDTO.getCitaId() == null) {
            throw new BadRequestException("Se requiere el ID de la cita para vincular el tiraje.");
        }

        Cita cita = citaRepository.findById(tirajeDTO.getCitaId())
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con ID: " + tirajeDTO.getCitaId()));

        if (cita.getEstado() != EstadoCita.PENDIENTE) {
            throw new BadRequestException("Solo se puede realizar el tiraje en citas con estado PENDIENTE.");
        }

        Consulta consulta = consultaMapper.toEntity(tirajeDTO);


        consulta.setFechaAtencion(LocalDateTime.now());
        consulta.setPaciente(cita.getPaciente());


        // la enfermera los inicializa temporalmente como "PENDIENTE"
        consulta.setDiagnostico("PENDIENTE DOCTOR");
        consulta.setTratamiento("PENDIENTE DOCTOR");
        consulta.setNota(tirajeDTO.getNota());

        Consulta consultaGuardada = consultaRepository.save(consulta);

        cita.setConsulta(consultaGuardada);
        cita.setEstado(EstadoCita.EN_ESPERA);
        citaRepository.save(cita);

        return consultaMapper.toDTO(consultaGuardada, cita.getId());
    }

    @Override
    @Transactional
    public ConsultaDTO finalizarConsultaDoctor(Integer citaId, ConsultaDTO consultaDTO) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con el ID: " + citaId));

        if (cita.getEstado() != EstadoCita.EN_ESPERA) {
            throw new BadRequestException("Esta consulta no se puede finalizar porque la cita no está en estado EN_ESPERA.");
        }

        Consulta consulta = cita.getConsulta();
        if (consulta == null) {
            throw new ResourceNotFoundException("No se puede proceder: el paciente aún no ha pasado por el control de signos vitales (triaje).");
        }

        if (consultaDTO.getDiagnostico() == null || consultaDTO.getDiagnostico().isBlank()) {
            throw new BadRequestException("El diagnóstico médico es obligatorio.");
        }
        if (consultaDTO.getTratamiento() == null || consultaDTO.getTratamiento().isBlank()) {
            throw new BadRequestException("El tratamiento médico es obligatorio.");
        }

        var doctor = doctorRepository.findById(consultaDTO.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("El doctor con ID " + consultaDTO.getDoctorId() + " no está registrado."));

        consulta.setDiagnostico(consultaDTO.getDiagnostico());
        consulta.setTratamiento(consultaDTO.getTratamiento());
        consulta.setNota(consultaDTO.getNota());
        consulta.setDoctor(doctor);

        Consulta consultaFinalizada = consultaRepository.save(consulta);

        cita.setEstado(EstadoCita.ATENDIDA);
        citaRepository.save(cita);

        return consultaMapper.toDTO(consultaFinalizada, cita.getId());
    }


    @Override
    @Transactional
    public ConsultaDTO editarTirajeEnfermera(Integer id, ConsultaDTO tirajeDTO) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta no encontrada con ID: " + id));

        Cita cita = citaRepository.findByConsultaId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita asociada no encontrada."));

        if (cita.getEstado() == EstadoCita.ATENDIDA) {
            throw new BadRequestException("No es posible editar los datos del tiraje porque la consulta ya ha sido cerrada por el doctor.");
        }

        consulta.setTirajePa(tirajeDTO.getTirajePa());
        consulta.setTirajeTemperatura(tirajeDTO.getTirajeTemperatura());
        consulta.setTirajePeso(tirajeDTO.getTirajePeso());
        consulta.setTirajeEstatura(tirajeDTO.getTirajeEstatura());
        consulta.setTirajeSintomas(tirajeDTO.getTirajeSintomas());

        return consultaMapper.toDTO(consultaRepository.save(consulta), cita.getId());
    }

    @Override
    @Transactional
    public ConsultaDTO editarConsultaDoctor(Integer id, ConsultaDTO consultaDTO) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta no encontrada con ID: " + id));

        if (consultaDTO.getDiagnostico() == null || consultaDTO.getDiagnostico().isBlank()) {
            throw new BadRequestException("El diagnóstico no puede guardarse vacío.");
        }
        if (consultaDTO.getTratamiento() == null || consultaDTO.getTratamiento().isBlank()) {
            throw new BadRequestException("El tratamiento no puede guardarse vacío.");
        }

        consulta.setDiagnostico(consultaDTO.getDiagnostico());
        consulta.setTratamiento(consultaDTO.getTratamiento());
        consulta.setNota(consultaDTO.getNota());

        return consultaMapper.toDTO(consultaRepository.save(consulta), null);
    }

    @Override
    public List<ConsultaDTO> findByPacienteId(Integer pacienteId) {
        if (!pacienteRepository.existsById(pacienteId)) {
            throw new ResourceNotFoundException("El paciente con ID " + pacienteId + "no esta tegistrado.");
        }

        return consultaRepository.findByPacienteIdOrderByFechaAtencionDesc(pacienteId).stream()
                .map(con -> consultaMapper.toDTO(con, null))
                .toList();
    }

    @Override
    public List<ConsultaDTO> findByDoctorId(Integer doctorId) {
        return consultaRepository.findByDoctorIdOrderByFechaAtencionDesc(doctorId).stream()
                .map(con -> consultaMapper.toDTO(con, null))
                .toList();
    }
}
