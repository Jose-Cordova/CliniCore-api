package com.clinicore.CliniCore_api.services;

import com.clinicore.CliniCore_api.dto.EspecialidadDTO;
import com.clinicore.CliniCore_api.entities.Especialidad;
import com.clinicore.CliniCore_api.exceptions.BadRequestException;
import com.clinicore.CliniCore_api.exceptions.ResourceNotFoundException;
import com.clinicore.CliniCore_api.interfaces.IEspecialidadService;
import com.clinicore.CliniCore_api.mappers.EspecialidadMapper;
import com.clinicore.CliniCore_api.repository.EspecialidadRepositiry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EspecialidadService implements IEspecialidadService {
    private final EspecialidadRepositiry repositiry;
    private final EspecialidadMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<EspecialidadDTO> findAll() {
        //Retornamos todas las especialidades
        return mapper.toDtoList(repositiry.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public EspecialidadDTO findById(Integer id) {
        //Retornamos solo una especialidad
        Especialidad entity = repositiry.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marca no encontrada con ID: " + id));
        return mapper.toDTO(entity);
    }

    @Override
    @Transactional
    public EspecialidadDTO saveOrUpdate(EspecialidadDTO dto) {
        //Validamos que nombre no sea nulo
        if(dto.getNombre() == null || dto.getNombre().isBlank()){
            throw new BadRequestException("El nombre de la especialidad es obligatorio");
        }
        //Validamos que no se duplique el nombre
        if(dto.getId() == null && repositiry.existsByNombre(dto.getNombre())){
            throw new BadRequestException("Ya existe una especialidad con el nombre " + dto.getNombre());
        }
        //Validamos que no se duplique por actualizacion
        if(dto.getId() != null){
            //Validamos que el id exista
            repositiry.findById(dto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("No existe la marca con ID: " + dto.getId()));
        }
        if(repositiry.existsByNombreAndIdNot(dto.getNombre(), dto.getId())){
            throw new BadRequestException("Ya existe otra especialidad con el nombre " + dto.getNombre());
        }
        //Convertimos el dto a entidad
        Especialidad especialidad = mapper.toEntity(dto);
        return mapper.toDTO(repositiry.save(especialidad));
    }

    @Override
    public void delete(Integer id) {
        //Buscamos la especialidad en la db
        Especialidad entity = repositiry.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la especialidad con ID: " + id));
        repositiry.delete(entity);
    }
}
