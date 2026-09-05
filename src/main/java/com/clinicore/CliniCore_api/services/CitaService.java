package com.clinicore.CliniCore_api.services;


import com.clinicore.CliniCore_api.dto.CitaDTO;
import com.clinicore.CliniCore_api.entities.Cita;
import com.clinicore.CliniCore_api.entities.Disponibilidad;
import com.clinicore.CliniCore_api.enums.EstadoCita;
import com.clinicore.CliniCore_api.enums.EstadoDisponibilidad;
import com.clinicore.CliniCore_api.exceptions.BadRequestException;
import com.clinicore.CliniCore_api.exceptions.ConflictException;
import com.clinicore.CliniCore_api.exceptions.ResourceNotFoundException;
import com.clinicore.CliniCore_api.interfaces.ICitaService;
import com.clinicore.CliniCore_api.mappers.CitaMapper;
import com.clinicore.CliniCore_api.repository.CitaRepository;
import com.clinicore.CliniCore_api.repository.DisponibilidadRepository;
import com.clinicore.CliniCore_api.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CitaService implements ICitaService {


    private final CitaRepository citaRepository;
    private final DisponibilidadRepository disponibilidadRepository;
    private final PacienteRepository pacienteRepository;
    private final CitaMapper citaMapper;


    @Override
    @Transactional(readOnly = true)
    public List<CitaDTO> findAll() {
        return citaRepository.findAll().stream()
                .map(cita -> {
                    Disponibilidad disponibilidad = disponibilidadRepository.findByCitaId(cita.getId()).orElse(null);
                    return citaMapper.toDTO(cita, disponibilidad);
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CitaDTO findById(Integer id) {

        //Buscamos en el repositorio la disponibilidad y usamos el id de la cita
        //Si no se encuentra o no existe la cita devolvemos un error 404
       Disponibilidad disponibilidad = disponibilidadRepository.findByCitaId(id)
               .orElseThrow(()-> new ResourceNotFoundException("No se encontró la cita con el ID:" + id));

       //la disponiblidad ya tiene le relacion mapeada a la cita
        // extraemos el objecto que ya se nos carga de manera automatica
        Cita cita = disponibilidad.getCita();

        //Se mape ay etornamos el DTO
        return citaMapper.toDTO(cita, disponibilidad);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaDTO> findByPacienteId(Integer pacienteId) {
        return citaRepository.findByPacienteId(pacienteId).stream()
                .map(cita -> {
                    Disponibilidad disponibilidad = disponibilidadRepository.findByCitaId(cita.getId()).orElse(null);
                    return citaMapper.toDTO(cita, disponibilidad);
                })
                .toList();
    }

    @Override
    public List<CitaDTO> findByDoctorId(Integer doctorId) {
        List<Cita> todasLasCitas = citaRepository.findAll();
        List<CitaDTO> citasDoctor = new ArrayList<>();

        for(Cita cita : todasLasCitas) {
            Disponibilidad disponibilidad = disponibilidadRepository.findByCitaId(cita.getId()).orElse(null);
            if(disponibilidad != null && disponibilidad.getDoctor() != null && disponibilidad.getDoctor().getId().equals(doctorId)) {
                citasDoctor.add(citaMapper.toDTO(cita, disponibilidad));
            }
        }

        return citasDoctor;
    }


    @Override
    @Transactional
    public CitaDTO agendarCita(CitaDTO requestDTO) {

        Disponibilidad disponibilidad = disponibilidadRepository.findByIdConBloqueo(requestDTO.getDisponibilidadId().longValue())
                .orElseThrow(() -> new BadRequestException("El horario de disponibilidad seleccionado no existe."));

        if (disponibilidad.getEstado() == EstadoDisponibilidad.OCUPADO) {
            throw new ConflictException("El horario seleccionado ya ha sido ocupado.");
        }

        var paciente = pacienteRepository.findById(requestDTO.getPacienteId())
                .orElseThrow(() -> new ResourceNotFoundException("El paciente con ID " + requestDTO.getPacienteId() + " no está registrado."));

        Cita nuevaCita = citaMapper.toEntity(requestDTO);
        nuevaCita.setPaciente(paciente);
        nuevaCita.setDoctor(disponibilidad.getDoctor()); //Se ha agregado nuevo
        nuevaCita.setEstado(EstadoCita.PENDIENTE);
        Cita citaGuardada = citaRepository.save(nuevaCita);

        disponibilidad.setCita(citaGuardada);
        disponibilidad.setEstado(EstadoDisponibilidad.OCUPADO);
        disponibilidadRepository.save(disponibilidad);

        return citaMapper.toDTO(citaGuardada, disponibilidad);
    }

    @Override
    @Transactional
    public CitaDTO cambiarEstado(Integer id, EstadoCita nuevoEstado) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada para cambiar de estado con el ID: " + id));

        validarTransicion(cita.getEstado(), nuevoEstado);
        cita.setEstado(nuevoEstado);
        Cita citaActualizada = citaRepository.save(cita);

        Disponibilidad disponibilidad = disponibilidadRepository.findByCitaId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la disponibilidad asociada a la cita con ID: " + id));

        return citaMapper.toDTO(citaActualizada, disponibilidad);
    }

    @Override
    @Transactional
    public CitaDTO cancelarCita(Integer id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con ID: " + id));

        validarTransicion(cita.getEstado(), EstadoCita.CANCELADA);

        Disponibilidad disponibilidad = disponibilidadRepository.findByCitaId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puco econtrar la disponibilidad asignada a esta cita."));

        disponibilidad.setEstado(EstadoDisponibilidad.DISPONIBLE);
        disponibilidad.setCita(null);
        disponibilidadRepository.save(disponibilidad);

        cita.setEstado(EstadoCita.CANCELADA);
        Cita citaCancelada = citaRepository.save(cita);

        return citaMapper.toDTO(citaCancelada, null);
    }

    @Override
    @Transactional
    public CitaDTO reasignarCita(Integer citaId, Integer nuevoHorarioId) {
        Cita citaOriginal = citaRepository.findById(citaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con ID: " + citaId));

        validarTransicion(citaOriginal.getEstado(), EstadoCita.REASIGNADA);

        Disponibilidad disponibilidadVieja = disponibilidadRepository.findByCitaId(citaId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el horario asignado a la cita a modificar."));

        Disponibilidad disponibilidadNueva = disponibilidadRepository.findByIdConBloqueo(nuevoHorarioId.longValue())
                .orElseThrow(() -> new ResourceNotFoundException("La nueva disponibilidad seleccionada no existe."));

        if (disponibilidadNueva.getEstado() == EstadoDisponibilidad.OCUPADO) {
            throw new ConflictException("El médico de reemplazo ya fue ocupado en este horario.");
        }

        disponibilidadVieja.setEstado(EstadoDisponibilidad.DISPONIBLE);
        disponibilidadVieja.setCita(null);
        disponibilidadRepository.save(disponibilidadVieja);

        citaOriginal.setEstado(EstadoCita.REASIGNADA);
        citaOriginal.setDoctor(disponibilidadNueva.getDoctor()); //Se agrego tambien
        Cita citaModificada = citaRepository.save(citaOriginal);

        disponibilidadNueva.setCita(citaModificada);
        disponibilidadNueva.setEstado(EstadoDisponibilidad.OCUPADO);
        disponibilidadRepository.save(disponibilidadNueva);

        return citaMapper.toDTO(citaModificada, disponibilidadNueva);
    }

    private void validarTransicion(EstadoCita estadoActual, EstadoCita nuevoEstado) {
        boolean valida = switch (estadoActual) {
            case ATENDIDA -> false;  // Citas cerradas no se pueden alterar
            case CANCELADA -> false; // Citas canceladas no se pueden reactivar

            // El Paciente agenda -> Puede pasar a EN_ESPERA (Triaje) o CANCELADA directamente
            case PENDIENTE -> nuevoEstado == EstadoCita.EN_ESPERA || nuevoEstado == EstadoCita.CANCELADA || nuevoEstado == EstadoCita.REASIGNADA;

            // La Enfermera hizo el triaje -> Solo el Médico puede pasarla a ATENDIDA, o el paciente puede CANCELARLA si se va
            case EN_ESPERA -> nuevoEstado == EstadoCita.ATENDIDA || nuevoEstado == EstadoCita.CANCELADA;

            // El Doctor reasignó -> El nuevo médico la tomará y pasará a ATENDIDA
            case REASIGNADA -> nuevoEstado == EstadoCita.ATENDIDA;
        };

        if (!valida) {
            throw new BadRequestException("Flujo de negocio inválido: No se permite cambiar el estado de la cita desde " + estadoActual + " hacia " + nuevoEstado);
        }
    }
}
