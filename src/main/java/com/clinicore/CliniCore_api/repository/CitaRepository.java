package com.clinicore.CliniCore_api.repository;

import com.clinicore.CliniCore_api.entities.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Integer> {
    List<Cita> findByPacienteId(Integer pacienteId);

    Optional<Cita> findByConsultaId(Integer consultaId);


}
