package com.clinicore.CliniCore_api.repository;

import com.clinicore.CliniCore_api.entities.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Integer> {
    List<Cita> findByPacienteId(Integer pacienteId);
}
