package com.clinicore.CliniCore_api.interfaces;

import com.clinicore.CliniCore_api.dto.EspecialidadDTO;

import java.util.List;

public interface IEspecialidadService {
    //Obtenemos todas las especialidades
    List<EspecialidadDTO> findAll();
    //Obtenemos una especialidad
    EspecialidadDTO findById(Integer id);
    //Creamos/Actualizamos una especialidad
    EspecialidadDTO saveOrUpdate(EspecialidadDTO dto);
    //Eliminamos una especialidad
    void delete(Integer id);
}
