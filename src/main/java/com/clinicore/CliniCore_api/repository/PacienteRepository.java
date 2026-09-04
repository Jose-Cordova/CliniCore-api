package com.clinicore.CliniCore_api.repository;

import com.clinicore.CliniCore_api.entities.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Integer> {

    Optional<Paciente> findByUsuario_Id(Integer usuarioId);
    boolean existsByDui(String dui);
    boolean existsByTelefono(String telefono);
    // Busca un paciente por su DUI para evitar duplicados
    Optional<Paciente> findByDui(String dui);

    // Busca un paciente por su código de expediente único
    Optional<Paciente> findByCodigoExpediente(String codigoExpediente);

    @Query("SELECT COUNT(p) FROM Paciente p WHERE p.fechaRegistro BETWEEN :inicio AND :fin")
    long countByFechaRegistroBetween(
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin
    );
    // Obtiene el último paciente registrado con el código de expediente más alto
    Optional<Paciente> findFirstByOrderByCodigoExpedienteDesc();

    // Verifica si ya existe otro paciente con el mismo DUI pero con diferente ID (para actualización)
    boolean existsByDuiAndIdNot(String dui, Integer id);
}