package com.clinicore.CliniCore_api.repository;


import com.clinicore.CliniCore_api.entities.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Integer> {

    //Obtiene el expediente clinico completo del paciente
    List<Consulta> findByPacienteIdOrderByFechaAtencionDesc(Integer pacienteId);

    //Obtiene el historial de consultas de un doctor
    List<Consulta> findByDoctorIdOrderByFechaAtencionDesc(Integer doctorId);

    // Obtiene las consultas firmadas o iniciadas por un médico específico cruzando las citas
    List<Consulta> findConsultasByDoctorId(@Param("doctorId") Integer doctorId);

}
