package com.clinicore.CliniCore_api.interfaces;

import com.clinicore.CliniCore_api.dto.DoctorDTO;

import java.util.List;

public interface IDoctorService {
    //Obtenemos todos los doctores
    List<DoctorDTO> findAll();
    //Obtenemos un doctor
    DoctorDTO findById(Integer id);
    //Creamos/Actualizamos un doctor
    DoctorDTO saveOrUpdate(DoctorDTO dto);
    //Eliminamos un doctor
    void delete(Integer id);
}
