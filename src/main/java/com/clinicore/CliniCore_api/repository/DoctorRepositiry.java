package com.clinicore.CliniCore_api.repository;

import com.clinicore.CliniCore_api.entities.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepositiry extends JpaRepository<Doctor, Integer> {
    boolean existsByCodigoAndIdNot(String codigo, Integer id);
    boolean existsByEspecialidadId(Integer especialidadId);
}
