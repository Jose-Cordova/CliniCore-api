package com.clinicore.CliniCore_api.repository;

import com.clinicore.CliniCore_api.entities.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
    Optional<Doctor> findByUsuario_Id(Integer usuarioId);
    boolean existsByTelefono(String telefono);
    boolean existsByCodigo(String codigo);
    boolean existsByCodigoAndIdNot(String codigo, Integer id);
    boolean existsByEspecialidadId(Integer especialidadId);
}