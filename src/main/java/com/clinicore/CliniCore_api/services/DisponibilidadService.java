package com.clinicore.CliniCore_api.services;

import com.clinicore.CliniCore_api.dto.DisponibilidadDTO;
import com.clinicore.CliniCore_api.entities.Disponibilidad;
import com.clinicore.CliniCore_api.entities.Doctor;
import com.clinicore.CliniCore_api.entities.HorarioBase;
import com.clinicore.CliniCore_api.enums.DiaSemana;
import com.clinicore.CliniCore_api.enums.EstadoDisponibilidad;
import com.clinicore.CliniCore_api.exceptions.BadRequestException;
import com.clinicore.CliniCore_api.exceptions.ResourceNotFoundException;
import com.clinicore.CliniCore_api.interfaces.IDisponibilidadService;
import com.clinicore.CliniCore_api.repository.DisponibilidadRepository;
import com.clinicore.CliniCore_api.repository.DoctorRepositiry;
import com.clinicore.CliniCore_api.repository.HorarioBaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DisponibilidadService implements IDisponibilidadService {
    //Inyeccion de dependecias de los repositorios
    private final DisponibilidadRepository disponibilidadRepository;
    private final DoctorRepositiry doctorRepositiry;
    private final HorarioBaseRepository horarioBaseRepository;

    @Override
    @Transactional
    public int generarDisponibilidadesPorRango(Integer doctorId, LocalDate fechaInicio, LocalDate fechaFin) {
        //Validar parametros nulos
        if (doctorId == null || fechaInicio == null || fechaFin == null) {
            throw new BadRequestException("El doctorId, fechaInicio y fechaFin son obligatorios.");
        }
        //Validar que fechaInicio no sea posterior a fechaFin
        if (fechaInicio.isAfter(fechaFin)) {
            throw new BadRequestException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }
        //Validar que no se generen slots para fechas pasadas
        if (fechaInicio.isBefore(LocalDate.now())) {
            throw new BadRequestException("No se pueden generar disponibilidades para fechas pasadas.");
        }
        //Validar rango maximo razonable (7 dias, planificacion semanal)
        if (fechaInicio.plusDays(7).isBefore(fechaFin)) {
            throw new BadRequestException("El rango máximo permitido para generar disponibilidades es de 7 días.");
        }
        //Buscamos el doctor en la db
        Doctor doctor = doctorRepositiry.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor no encontrado con id: " + doctorId));
        //Obtenemos la plantilla base configurada por el doctor
        List<HorarioBase> horarios = horarioBaseRepository.findByDoctorId(doctorId);
        //Validar que el doctor tenga al menos un horario base configurado
        if (horarios.isEmpty()) {
            throw new BadRequestException("El doctor no tiene configurado ningún horario base de atención.");
        }
        //Convertimos la lista de horarios a un map por dia de la semana para busqueda mas rapida
        Map<DiaSemana, HorarioBase> mapHorarios = horarios.stream()
                .collect(Collectors.toMap(HorarioBase::getDiaSemana, h -> h));
        //Lista donde guardaremos llos slots generados
        List<Disponibilidad> nuevasDisponibilidades = new ArrayList<>();
        //Inicializamos la variablle del bucle con la fecha inicial de del rango
        LocalDate fechaActual = fechaInicio;

        //Recorremos dia por dia hasta llegar a la fecha fin
        while(!fechaActual.isAfter(fechaFin)){
            // Mapeamos el DayOfWeek de Java al Enum DiaSemana en español
            DiaSemana diaSemana = switch (fechaActual.getDayOfWeek()) {
                case MONDAY -> DiaSemana.LUNES;
                case TUESDAY -> DiaSemana.MARTES;
                case WEDNESDAY -> DiaSemana.MIERCOLES;
                case THURSDAY -> DiaSemana.JUEVES;
                case FRIDAY -> DiaSemana.VIERNES;
                default -> null; // Sábado y Domingo no aplican si el horario es L-V
            };

            //Buscamos si el doctor horario para ese dia
            HorarioBase horario = diaSemana != null ? mapHorarios.get(diaSemana) : null;
            if(horario != null && !disponibilidadRepository.existsByDoctorIdAndFecha(doctorId, fechaActual)){
                LocalTime slotInicio = horario.getHoraInicio();
                LocalTime horaFinJornada = horario.getHoraFin();

                //Interamos mientras el slot de 30 minutos no supere la hora fin
                while(slotInicio.plusMinutes(30).isBefore(horaFinJornada) || slotInicio.plusMinutes(30).equals(horaFinJornada)){
                    LocalTime slotFin = slotInicio.plusMinutes(30);
                    //Validamos si el slot cae dentro de la hora de almuerzo
                    boolean esAlmuerzo = horario.getHoraAlmuerzoInicio() != null
                            && horario.getHoraAlmuerzoFin() != null
                            && !(slotFin.isBefore(horario.getHoraAlmuerzoInicio()) || slotInicio.isAfter(horario.getHoraAlmuerzoFin()));
                    if(!esAlmuerzo){
                        nuevasDisponibilidades.add(Disponibilidad.builder()
                                .doctor(doctor)
                                .fecha(fechaActual)
                                .horaInicio(slotInicio)
                                .horaFin(slotFin)
                                .estado(EstadoDisponibilidad.DISPONIBLE)
                                .build()
                        );
                    }
                    slotInicio = slotFin;
                }
            }
            //Pasamos al siguiente dia del calendario
            fechaActual = fechaActual.plusDays(1);
        }
        //Si se generan slots nuevos guardarlos todos en lote
        if(!nuevasDisponibilidades.isEmpty()){
            disponibilidadRepository.saveAll(nuevasDisponibilidades);
        }
        return nuevasDisponibilidades.size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DisponibilidadDTO> obtenerDisponiblesPorDoctorFecha(Integer doctorId, LocalDate fecha) {
        //Consultamos unicamente los slots disponibles de ese doctor por la fecha
        return disponibilidadRepository.findByDoctorIdAndFechaAndEstadoOrderByHoraInicio(
                doctorId, fecha, EstadoDisponibilidad.DISPONIBLE
        ).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DisponibilidadDTO> obtenerDisponiblesPorEspecialidadFecha(Integer especialidadId, LocalDate fecha) {
        //Consultamos slost disponibles por especialidad
        return disponibilidadRepository.findByDoctorEspecialidadIdAndFechaAndEstadoOrderByHoraInicio(
                especialidadId, fecha, EstadoDisponibilidad.DISPONIBLE
        ).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    //Metodo auxiliar para mappear la entidad Disponibilidad a su DTO de salida
    private DisponibilidadDTO mapToDTO(Disponibilidad d){
        DisponibilidadDTO dto = new DisponibilidadDTO();
        dto.setId(d.getId());
        dto.setFecha(d.getFecha());
        dto.setHoraInicio(d.getHoraInicio());
        dto.setHoraFin(d.getHoraFin());
        dto.setEstado(d.getEstado());
        //Asignar id de cita existente y si no sera null
        dto.setCitaId(d.getCita() != null ? d.getCita().getId() : null);
        dto.setDoctorId(d.getDoctor().getId());
        //Contanenar nombre completo del doctor
        dto.setDoctorNombre(d.getDoctor().getNombre() + " " + d.getDoctor().getApellido());
        return dto;
    }
}
