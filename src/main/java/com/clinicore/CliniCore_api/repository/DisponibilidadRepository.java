package com.clinicore.CliniCore_api.repository;

import com.clinicore.CliniCore_api.entities.Disponibilidad;
import com.clinicore.CliniCore_api.enums.EstadoDisponibilidad;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long> {
    List<Disponibilidad> findByDoctorIdAndFechaOrderByHoraInicio(Integer doctorId, LocalDate fecha);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from Disponibilidad d where d.id = :id")
    Optional<Disponibilidad> findByIdConBloqueo(@Param("id") Long id);

    //Slot libre exacto de un doctor en fecha+hora (para reasignar y reservar)
    Optional<Disponibilidad> findByDoctorIdAndFechaAndHoraInicioAndEstado(
            Integer doctorId, LocalDate fecha, LocalTime horaInicio, EstadoDisponibilidad estado);

    //Slot libre más cercano: los libres del doctor ese día, ordenados por hora
    List<Disponibilidad> findByDoctorIdAndFechaAndEstadoOrderByHoraInicio(
            Integer doctorId, LocalDate fecha, EstadoDisponibilidad estado);
    //Busqueda para pacientes
    List<Disponibilidad> findByDoctorEspecialidadIdAndFechaAndEstadoOrderByHoraInicio(
            Integer especialidadId, LocalDate fecha, EstadoDisponibilidad estado);

    //Único slot que ocupa una cita (para liberar o bloquear al reasignar)
    Optional<Disponibilidad> findByCitaId(Integer citaId);

    //Verificar si ya se generaron sllots para evitar dupllicados
    boolean existsByDoctorIdAndFecha(Integer doctorId, LocalDate fecha);

    void deleteByDoctorIdAndFecha(Integer doctorId, LocalDate fecha);
}
