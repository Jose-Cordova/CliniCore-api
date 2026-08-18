package com.clinicore.CliniCore_api.repository;

import com.clinicore.CliniCore_api.entities.HorarioBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HorarioBaseRepository extends JpaRepository<HorarioBase, Integer> {
    List<HorarioBase> findByDoctorId(Integer doctorId);
}
